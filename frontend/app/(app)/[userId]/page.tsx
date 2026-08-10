import { cookies } from 'next/headers';
import { notFound, redirect } from 'next/navigation';

import Avatar from '../../components/Avatar';
import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import { AVATAR_RING_CLASSES } from './avatarRing';
import EditAvatarButton from './EditAvatarButton';
import EditDisplayNameButton from './EditDisplayNameButton';

interface Props {
  params: Promise<{ userId: string }>;
}

// The friends LIST left this file at unit 2a; the friends COUNT comes back here
// at unit 27, because the export's profile shows it as a row in the info card.
// One record is enough - `totalElements` describes the whole collection, so the
// page size is set to the smallest value the backend accepts rather than 10.
async function loadFriendsCount(): Promise<number | null> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page: 0, size: 1 } },
  });

  if (!response.ok) return null;
  return data?.totalElements ?? 0;
}

// A row of the export's info card: label left, value right, hairline under.
// The label colour IS a token - `hub-muted` is exactly the #7c8a92 the export
// uses - which makes this the one place in Etap D where the export's secondary
// text maps onto something we already had.
function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="border-elevated-border px-5.5 py-4.5 flex justify-between border-b last:border-b-0">
      <span className="text-hub-muted text-sm font-semibold">{label}</span>
      <span className="text-sm font-bold">{value}</span>
    </div>
  );
}

// Everything to do with the friends list left this file at unit 2a and lives at
// app/(app)/friends/. The page no longer takes `searchParams` because the only
// query parameter it ever read was the friends pager's `?page=`.
export default async function UserProfilePage({ params }: Props) {
  const { userId } = await params;

  const cookieStore = await cookies();
  const signedInUserId = cookieStore.get('user_id')?.value ?? null;
  const isOwnProfile = signedInUserId !== null && signedInUserId === userId;

  const { data, response } = await client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  });

  if (response.status === 401 || response.status === 403) {
    redirect('/auth/logout');
  }

  if (response.status === 404 || !data) {
    notFound();
  }

  const displayName = data.displayName ?? 'Unknown User';
  const avatarSrc = data.avatarId ? `/api/users/avatar/${data.avatarId}` : null;

  // GET /friends always returns the caller's own friends, so the count is only
  // meaningful on your own profile - the same reason unit 2a could drop the
  // route parameter entirely when it moved the list out.
  const friendsCount = isOwnProfile ? await loadFriendsCount() : null;

  // Three structural changes from the export (isProfile, lines 280-304):
  // the route gains its own h1, the banner becomes the export's gradient hero
  // with the avatar leading, and the info card below it is entirely new.
  //
  // Logout also moves: it was a white button inside the banner, and the export
  // puts it alone at the bottom right, outside the cards.
  return (
    <div className="flex min-w-0 w-full flex-1 flex-col gap-5 lg:gap-7">
      {/* The heading has to answer "whose profile is this", because the route
          serves both. Unit 27 promoted a static "My Profile" into this slot and
          the non-owner branch was not revisited, so every stranger's page
          claimed to be yours - the three blocks below are all gated on
          `isOwnProfile`, and this was the one that was not. It also cost the
          accessible name: before this h1 existed the display name WAS the h1,
          which was right for both cases, and a screen reader navigating by
          heading heard "My Profile" on someone else's page. */}
      <h1 className="text-2xl font-extrabold tracking-tight lg:text-3xl">
        {isOwnProfile ? 'My Profile' : `${displayName}'s profile`}
      </h1>

      {/* Hero. The gradient and its shadow are the export's; the shadow IS a
          dictionary row ("element podniesiony na gradiencie"), the gradient is
          an arbitrary value because none of its three stops has a token. */}
      <div className="flex items-center gap-7 rounded-3xl bg-[linear-gradient(135deg,#146b7a,#2f9bcf_55%,#4ac9a0)] p-6 shadow-[0_12px_32px_rgba(10,42,77,0.18)] lg:p-9">
        {isOwnProfile ? (
          <EditAvatarButton
            avatarId={data.avatarId}
            displayName={displayName}
          />
        ) : (
          <div className={`shrink-0 ${AVATAR_RING_CLASSES}`}>
            <Avatar src={avatarSrc} alt={`${displayName}'s avatar`} size={96} />
          </div>
        )}

        <div className="flex min-w-0 flex-col gap-2.5">
          {isOwnProfile ? (
            <EditDisplayNameButton displayName={displayName} />
          ) : (
            <p className="truncate text-2xl font-extrabold text-white">
              {displayName}
            </p>
          )}
        </div>
      </div>

      <div className="bg-elevated-surface text-on-elevated-surface flex flex-col rounded-[20px] p-2 shadow-[0_8px_24px_rgba(10,42,77,0.08)]">
        {/* The export's card has three rows: Email, Username, Friends. Email is
            missing because the data is: `/users/{userId}/details` returns only
            `displayName` and `avatarId`, confirmed by the generated types.
            Showing a placeholder would be worse than showing nothing - it would
            imply the field exists and is empty. Recorded in 12.5. */}
        <InfoRow label="Username" value={displayName} />
        {friendsCount !== null && (
          <InfoRow label="Friends" value={String(friendsCount)} />
        )}
      </div>

      {isOwnProfile && (
        <form action={logout} className="flex justify-end">
          <button
            type="submit"
            className="px-7.5 py-3.25 rounded-[14px] bg-[linear-gradient(135deg,#e35b52,#c0453f)] text-sm font-bold text-white shadow-[0_8px_20px_rgba(192,69,63,0.28)] transition-[filter] hover:brightness-110"
          >
            Logout
          </button>
        </form>
      )}
    </div>
  );
}
