import { cookies } from 'next/headers';
import Image from 'next/image';
import { notFound, redirect } from 'next/navigation';

import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import EditAvatarButton from './EditAvatarButton';
import EditDisplayNameButton from './EditDisplayNameButton';
import FriendsPanel from './FriendsPanel';

interface Props {
  params: Promise<{ userId: string }>;
}

export default async function UserProfilePage({ params }: Props) {
  const { userId } = await params;

  const cookieStore = await cookies();
  const signedInUserId = cookieStore.get('user_id')?.value ?? null;
  const isOwnProfile = signedInUserId !== null && signedInUserId === userId;

  const { data, response } = await client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  });

  if (response.status === 401 || response.status === 403) {
    redirect('/login');
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
  let friends: Record<string, { displayName?: string; avatarId?: { val?: string } }> | undefined;
  let friendsLoadError = false;
  if (isOwnProfile) {
    // Workaround for the backend not reporting a total count on this endpoint:
    // there's no way to know from the response whether more friends exist
    // beyond the current page, so a real "load more" control can't be built.
    // Request the largest page the backend allows (size is capped at 20)
    // instead as a stopgap. Remove this once the backend returns paging
    // metadata (e.g. totalElements) alongside the friends map.
    const { data: friendsData, response: friendsResponse } = await client.GET(
      '/friends',
      { params: { query: { size: 20 } } },
    );
    friends = friendsData ?? undefined;
    friendsLoadError = !friendsResponse.ok;
  }

  return (
    <div className="flex min-w-0 flex-1 flex-col gap-6">
      {/* Profile banner */}
      <div className="bg-primary flex items-start justify-between rounded-2xl p-6">
        <div>
          {isOwnProfile ? (
            <EditDisplayNameButton displayName={displayName} />
          ) : (
            <h1 className="text-brand-reversed-main-color mb-4 text-3xl font-bold">
              {displayName}
            </h1>
          )}
          {isOwnProfile && (
            <form action={logout}>
              <button
                type="submit"
                className="bg-brand-reversed-main-color text-brand-main-color rounded-lg px-4 py-2 text-sm font-bold transition-colors hover:brightness-90"
              >
                Log Out
              </button>
            </form>
          )}
        </div>

        {isOwnProfile ? (
          <EditAvatarButton avatarId={data.avatarId} displayName={displayName} />
        ) : avatarSrc ? (
          <Image
            src={avatarSrc}
            alt={`${displayName}'s avatar`}
            width={96}
            height={96}
            className="h-24 w-24 rounded-full object-cover"
          />
        ) : (
          <div className="h-24 w-24 rounded-full bg-blue-200" />
        )}
      </div>

      {isOwnProfile &&
        (friendsLoadError ? (
          <p className="text-brand-reversed-main-color/40 text-sm">
            Couldn&apos;t load friends. Please try again later.
          </p>
        ) : (
          <FriendsPanel friends={friends ?? {}} currentUserId={userId} />
        ))}
    </div>
  );
}
