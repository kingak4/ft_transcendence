import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { client } from '../../lib/api-clients';
import FriendsPanel, { type FriendCard } from './FriendsPanel';

// The backend caps `size` at 20 and rejects 0; 10 matches its own default.
const FRIENDS_PAGE_SIZE = 10;

interface FriendsPage {
  cards: FriendCard[];
  page: number;
  totalPages: number;
  loadError: boolean;
}

// `?page=` is user-controlled, and the backend has no guard against a negative
// page (PageRequest.of throws, surfacing as a 500). Anything that isn't a
// non-negative integer falls back to page 0 before it reaches the API.
function parsePageParam(raw: string | undefined): number {
  const parsed = Number(raw);
  return Number.isInteger(parsed) && parsed >= 0 ? parsed : 0;
}

// Unwraps the backend's Spring `Page` envelope so FriendsPanel never has to
// know about it. Every field is optional in the generated types because Java
// can't express non-nullability to the OpenAPI generator, hence the fallbacks.
async function loadFriendsPage(page: number): Promise<FriendsPage> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page, size: FRIENDS_PAGE_SIZE } },
  });

  if (!response.ok) {
    return { cards: [], page, totalPages: 1, loadError: true };
  }

  return {
    cards: (data?.content ?? []).map((friend) => ({
      id: friend.id ?? '',
      displayName: friend.details?.displayName ?? 'Unknown User',
      avatarId: friend.details?.avatarId?.val,
    })),
    page: data?.number ?? page,
    totalPages: data?.totalPages ?? 1,
    loadError: false,
  };
}

interface Props {
  searchParams: Promise<{ page?: string }>;
}

// Split out of app/(app)/[userId]/page.tsx at unit 2a. Two things got simpler
// in the move, which is the sign the seam was in the right place:
//
// - GET /friends takes no userId - it always returns the caller's own friends,
//   whoever's profile you happened to be looking at. So this route needs no
//   route parameter, and the `isOwnProfile` guard that used to wrap the whole
//   panel disappears: here it is always your own list, by construction.
// - The signed-in id is now read from the cookie rather than inherited from the
//   URL. It is needed only so search can exclude you from its own results.
export default async function FriendsRoute({ searchParams }: Props) {
  const { page: pageParam } = await searchParams;
  const requestedPage = parsePageParam(pageParam);

  // (app)/layout.tsx already redirects when this is missing, so reaching here
  // without it should be impossible - but an empty string would silently break
  // self-exclusion in search rather than fail loudly, so it is re-checked.
  const cookieStore = await cookies();
  const signedInUserId = cookieStore.get('user_id')?.value ?? null;
  if (!signedInUserId) {
    redirect('/login');
  }

  const friendsPage = await loadFriendsPage(requestedPage);

  // Removing the last friend on a page leaves that page addressable but empty.
  // Bounce to the last page that still has rows instead of rendering nothing.
  if (
    !friendsPage.loadError &&
    friendsPage.cards.length === 0 &&
    friendsPage.page > 0
  ) {
    redirect(`/friends?page=${Math.max(0, friendsPage.totalPages - 1)}`);
  }

  // The export's equivalent screen is a 640px column with a 30px/800 heading and
  // a 24px gap - unified by 12.0 with the profile's 28px into 28/20. Page
  // padding is NOT repeated here: (app)/layout.tsx already supplies it, and
  // adding a second copy is the mistake positions 15 and 16 had to undo.
  //
  // The h1 is new. Until now this route had an h2 (inside FriendsPanel) and no
  // h1 at all - a heading-level gap, and a duplicate once the route named
  // itself. The panel's own heading went with this change.
  return (
    <div className="flex min-w-0 w-full flex-1 flex-col gap-5 lg:gap-7">
      <h1 className="text-2xl font-extrabold tracking-tight lg:text-3xl">
        Friends
      </h1>

      {friendsPage.loadError ? (
        <p className="text-on-surface/40 text-sm font-medium">
          Couldn&apos;t load friends. Please try again later.
        </p>
      ) : (
        <FriendsPanel
          key={friendsPage.page}
          friends={friendsPage.cards}
          currentUserId={signedInUserId}
          page={friendsPage.page}
          totalPages={friendsPage.totalPages}
        />
      )}
    </div>
  );
}
