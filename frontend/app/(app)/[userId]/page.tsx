import { cookies } from 'next/headers';
import { notFound, redirect } from 'next/navigation';

import Avatar from '../../components/Avatar';
import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import EditAvatarButton from './EditAvatarButton';
import EditDisplayNameButton from './EditDisplayNameButton';
import FriendsPanel, { type FriendCard } from './FriendsPanel';

interface Props {
  params: Promise<{ userId: string }>;
  searchParams: Promise<{ page?: string }>;
}

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

export default async function UserProfilePage({ params, searchParams }: Props) {
  const { userId } = await params;
  const { page: pageParam } = await searchParams;
  const requestedPage = parsePageParam(pageParam);

  const cookieStore = await cookies();
  const signedInUserId = cookieStore.get('user_id')?.value ?? null;
  const isOwnProfile = signedInUserId !== null && signedInUserId === userId;

  const { data, response } = await client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  });

  if (response.status === 401 || response.status === 403) {
    redirect('/api/auth/logout');
  }

  if (response.status === 404 || !data) {
    notFound();
  }

  const displayName = data.displayName ?? 'Unknown User';
  const avatarSrc = data.avatarId ? `/api/users/avatar/${data.avatarId}` : null;

  // Friends are only ever fetched for the signed-in user: GET /friends has
  // no userId in its path, so it always returns the caller's own friends
  // regardless of whose profile is being viewed. Fetching it for anyone
  // else's page would just show your friends mislabeled as theirs.
  const friendsPage = isOwnProfile
    ? await loadFriendsPage(requestedPage)
    : null;

  // Removing the last friend on a page leaves that page addressable but empty.
  // Bounce to the last page that still has rows instead of rendering nothing.
  if (
    friendsPage &&
    !friendsPage.loadError &&
    friendsPage.cards.length === 0 &&
    friendsPage.page > 0
  ) {
    redirect(`/${userId}?page=${Math.max(0, friendsPage.totalPages - 1)}`);
  }

  return (
    <div className="flex min-w-0 flex-1 flex-col gap-6">
      {/* Profile banner */}
      <div className="bg-primary flex items-start justify-between rounded-2xl p-6">
        <div>
          {isOwnProfile ? (
            <EditDisplayNameButton displayName={displayName} />
          ) : (
            <h1 className="text-on-primary mb-4 text-3xl font-bold">
              {displayName}
            </h1>
          )}
          {isOwnProfile && (
            <form action={logout}>
              <button
                type="submit"
                className="bg-elevated-surface text-on-elevated-surface rounded-lg px-4 py-2 text-sm font-bold transition-colors hover:brightness-90"
              >
                Log Out
              </button>
            </form>
          )}
        </div>

        {isOwnProfile ? (
          <EditAvatarButton
            avatarId={data.avatarId}
            displayName={displayName}
          />
        ) : (
          <Avatar src={avatarSrc} alt={`${displayName}'s avatar`} size={96} />
        )}
      </div>

      {friendsPage &&
        (friendsPage.loadError ? (
          <p className="text-on-surface/40 text-sm">
            Couldn&apos;t load friends. Please try again later.
          </p>
        ) : (
          <FriendsPanel
            key={friendsPage.page}
            friends={friendsPage.cards}
            currentUserId={userId}
            page={friendsPage.page}
            totalPages={friendsPage.totalPages}
          />
        ))}
    </div>
  );
}
