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

  // Workaround for the backend not reporting a total count on this endpoint:
  // there's no way to know from the response whether more friends exist
  // beyond the current page, so a real "load more" control can't be built.
  // Request the largest page the backend allows (size is capped at 20)
  // instead as a stopgap. Remove this once the backend returns paging
  // metadata (e.g. totalElements) alongside the friends map.
  const { data: friends, response: friendsResponse } = await client.GET(
    '/friends',
    { params: { query: { size: 20 } } },
  );

  const friendsLoadError = !friendsResponse.ok;

  return (
    <div className="flex min-w-0 flex-1 flex-col gap-6">
      {/* Profile banner */}
      <div className="bg-brand-secondary-color flex items-start justify-between rounded-2xl p-6">
        <div>
          <EditDisplayNameButton displayName={displayName} />
          <form action={logout}>
            <button
              type="submit"
              className="bg-brand-reversed-main-color text-brand-main-color rounded-lg px-4 py-2 text-sm font-bold transition-colors hover:brightness-90"
            >
              Log Out
            </button>
          </form>
        </div>

        <EditAvatarButton avatarId={data.avatarId} displayName={displayName} />
      </div>

      {friendsLoadError ? (
        <p className="text-brand-reversed-main-color/40 text-sm">
          Couldn&apos;t load friends. Please try again later.
        </p>
      ) : (
        <FriendsPanel friends={friends ?? {}} currentUserId={userId} />
      )}
    </div>
  );
}
