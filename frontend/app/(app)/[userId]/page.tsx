import { cookies } from 'next/headers';
import { notFound, redirect } from 'next/navigation';

import Avatar from '../../components/Avatar';
import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import EditAvatarButton from './EditAvatarButton';
import EditDisplayNameButton from './EditDisplayNameButton';

interface Props {
  params: Promise<{ userId: string }>;
}

// Everything to do with friends left this file at unit 2a and now lives at
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
    </div>
  );
}
