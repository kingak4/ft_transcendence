'use server';

import { getTranslations } from 'next-intl/server';

import { client } from '../../lib/api-clients';

export type ActionResult =
  | { success: true }
  | { success: false; message: string };

export async function removeFriendAction(
  friendId: string,
): Promise<ActionResult> {
  const { response } = await client.DELETE('/friends/{friendId}', {
    params: { path: { friendId } },
  });

  if (response.ok) return { success: true };
  const t = await getTranslations('errors');
  return { success: false, message: t('removeFriend') };
}

export async function addFriendAction(friendId: string): Promise<ActionResult> {
  const { response } = await client.POST('/friends/{friendId}', {
    params: { path: { friendId } },
  });

  if (response.ok) return { success: true };
  const t = await getTranslations('errors');
  return { success: false, message: t('addFriend') };
}

export type SearchUserResult = {
  id: string;
  displayName: string;
  avatarId?: string;
};

type SearchUsersResult =
  | { success: true; results: SearchUserResult[]; hasMore: boolean }
  | { success: false; message: string };

export async function searchUsersAction(
  query: string,
  page = 0,
  size = 10,
): Promise<SearchUsersResult> {
  const { data, response } = await client.GET('/users/search', {
    params: { query: { query, page, size } },
  });

  if (!response.ok || !data) {
    const t = await getTranslations('errors');
    return { success: false, message: t('searchUsers') };
  }

  const tCommon = await getTranslations('common');
  const results = (data.content ?? []).map((user) => ({
    id: user.id ?? '',
    displayName: user.displayName ?? tCommon('unknownUser'),
    avatarId: user.avatarId?.val,
  }));

  return { success: true, results, hasMore: !(data.last ?? true) };
}
