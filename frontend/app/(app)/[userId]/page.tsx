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

  const { data: friends, response: friendsResponse } =
    await client.GET('/friends');

  //if (friendsResponse.status === 401 || friendsResponse.status === 403) {
    //redirect('/login');
  //}

  const friendsLoadError = !friendsResponse.ok;

  // TEMP hardcode for screenshot — remove once the user_details_id join bug is fixed
  const friendsOverride: Record<
    string,
    { displayName?: string; avatarId?: { val?: string } }
  > = {
    '66b63bef-896b-4bd7-a91e-c56139228e2c': { displayName: 'User' },
    '3a8a7b20-b628-446d-b8b5-e783b640ee8d': {
      displayName: 'Ania',
      avatarId: { val: 'c1aace88-82b5-4ba7-b6f1-54cf1c56f676' },
    },
  };

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
        <FriendsPanel friends={friendsOverride} />
      )}
    </div>
  );
}
