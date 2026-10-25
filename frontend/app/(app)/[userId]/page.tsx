import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { cookies } from 'next/headers';
import { notFound, redirect } from 'next/navigation';
import { cache } from 'react';

import Avatar from '../../components/Avatar';
import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import { AVATAR_RING_CLASSES } from './avatarRing';
import EditAvatarButton from './EditAvatarButton';
import EditDisplayNameButton from './EditDisplayNameButton';

interface Props {
  params: Promise<{ userId: string }>;
}

// cache(): generateMetadata and the page share one request per render.
const loadUserDetails = cache((userId: string) =>
  client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  }),
);

async function profileTitle(isOwnProfile: boolean, displayName: string) {
  const t = await getTranslations('profile');
  return isOwnProfile
    ? t('myProfile')
    : t('userProfile', { name: displayName });
}

function isSignedInUser(
  cookieStore: Awaited<ReturnType<typeof cookies>>,
  userId: string,
) {
  return cookieStore.get('user_id')?.value === userId;
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { userId } = await params;
  const { data } = await loadUserDetails(userId);
  const t = await getTranslations('profile');
  if (!data) return { title: t('notFound') };

  const tCommon = await getTranslations('common');
  const isOwnProfile = isSignedInUser(await cookies(), userId);
  return {
    title: await profileTitle(
      isOwnProfile,
      data.displayName ?? tCommon('unknownUser'),
    ),
  };
}

async function loadFriendsCount(): Promise<number | null> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page: 0, size: 1 } },
  });

  if (!response.ok) return null;
  return data?.totalElements ?? 0;
}

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="border-elevated-border px-5.5 py-4.5 flex justify-between border-b last:border-b-0">
      <dt className="text-muted text-sm font-semibold">{label}</dt>
      <dd className="text-sm font-bold">
        <bdi>{value}</bdi>
      </dd>
    </div>
  );
}

export default async function UserProfilePage({ params }: Props) {
  const { userId } = await params;
  const t = await getTranslations('profile');
  const tCommon = await getTranslations('common');

  const isOwnProfile = isSignedInUser(await cookies(), userId);

  const { data, response } = await loadUserDetails(userId);

  if (response.status === 401 || response.status === 403) {
    redirect('/auth/logout');
  }

  if (response.status === 404 || !data) {
    notFound();
  }

  const displayName = data.displayName ?? tCommon('unknownUser');
  const avatarSrc = data.avatarId ? `/api/users/avatar/${data.avatarId}` : null;

  const friendsCount = isOwnProfile ? await loadFriendsCount() : null;

  return (
    <div className="flex min-w-0 w-full flex-1 flex-col gap-5 lg:gap-7">
      <h1 className="text-2xl font-extrabold tracking-tight lg:text-3xl">
        {await profileTitle(isOwnProfile, displayName)}
      </h1>

      <div className="bg-hero flex items-center gap-7 rounded-3xl p-6 shadow-[0_12px_32px_var(--color-shadow-hero)] lg:p-9">
        {isOwnProfile ? (
          <EditAvatarButton
            avatarId={data.avatarId}
            displayName={displayName}
          />
        ) : (
          <div className={`shrink-0 ${AVATAR_RING_CLASSES}`}>
            <Avatar
              src={avatarSrc}
              alt={tCommon('avatarAlt', { name: displayName })}
              size={96}
            />
          </div>
        )}

        <div className="flex min-w-0 flex-col gap-2.5">
          {isOwnProfile ? (
            <EditDisplayNameButton displayName={displayName} />
          ) : (
            <p className="text-on-accent truncate text-2xl font-extrabold">
              <bdi>{displayName}</bdi>
            </p>
          )}
        </div>
      </div>

      <dl className="bg-elevated-surface text-on-elevated-surface flex flex-col rounded-[20px] p-2 shadow-[0_8px_24px_var(--color-shadow-card)]">
        <InfoRow label={t('username')} value={displayName} />
        {friendsCount !== null && (
          <InfoRow label={t('friends')} value={String(friendsCount)} />
        )}
      </dl>

      {isOwnProfile && (
        <form action={logout} className="flex justify-end">
          <button
            type="submit"
            className="bg-danger-cta text-on-accent px-7.5 py-3.25 rounded-[14px] text-sm font-bold shadow-[0_8px_20px_color-mix(in_srgb,var(--color-danger-end)_28%,transparent)] transition-[filter] hover:brightness-110"
          >
            {t('logout')}
          </button>
        </form>
      )}
    </div>
  );
}
