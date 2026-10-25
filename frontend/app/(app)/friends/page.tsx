import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { client } from '../../lib/api-clients';
import FriendsPanel, { type FriendCard } from './FriendsPanel';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('friends');
  return { title: t('title') };
}

const FRIENDS_PAGE_SIZE = 10;

interface FriendsPage {
  cards: FriendCard[];
  page: number;
  totalPages: number;
  loadError: boolean;
}

function parsePageParam(raw: string | undefined): number {
  const parsed = Number(raw);
  return Number.isInteger(parsed) && parsed >= 0 ? parsed : 0;
}

async function loadFriendsPage(
  page: number,
  unknownUser: string,
): Promise<FriendsPage> {
  const { data, response } = await client.GET('/friends', {
    params: { query: { page, size: FRIENDS_PAGE_SIZE } },
  });

  if (!response.ok) {
    return { cards: [], page, totalPages: 1, loadError: true };
  }

  return {
    cards: (data?.content ?? []).map((friend) => ({
      id: friend.id ?? '',
      displayName: friend.details?.displayName ?? unknownUser,
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

export default async function FriendsRoute({ searchParams }: Props) {
  const { page: pageParam } = await searchParams;
  const requestedPage = parsePageParam(pageParam);

  const cookieStore = await cookies();
  const signedInUserId = cookieStore.get('user_id')?.value ?? null;
  if (!signedInUserId) {
    redirect('/login');
  }

  const t = await getTranslations('friends');
  const tCommon = await getTranslations('common');
  const friendsPage = await loadFriendsPage(
    requestedPage,
    tCommon('unknownUser'),
  );

  if (
    !friendsPage.loadError &&
    friendsPage.cards.length === 0 &&
    friendsPage.page > 0
  ) {
    redirect(`/friends?page=${Math.max(0, friendsPage.totalPages - 1)}`);
  }

  return (
    <div className="flex min-w-0 w-full flex-1 flex-col gap-5 lg:gap-7">
      <h1 className="text-2xl font-extrabold tracking-tight lg:text-3xl">
        {t('title')}
      </h1>

      {friendsPage.loadError ? (
        <p className="text-on-surface/40 text-sm font-medium">
          {t('loadError')}
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
