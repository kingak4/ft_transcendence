'use server';

import { client } from '../../lib/api-clients';

type ActionResult = { success: true } | { success: false; message: string };

export async function updateDisplayNameAction(
  displayName: string,
): Promise<ActionResult> {
  const { response } = await client.PATCH('/users/display-name', {
    body: { displayName },
  });

  if (response.ok) return { success: true };
  return {
    success: false,
    message: 'Failed to update display name. Try a different one.',
  };
}

export async function uploadAvatarAction(
  formData: FormData,
): Promise<ActionResult> {
  const file = formData.get('file') as File;
  const body = new FormData();
  body.append('file', file);

  const { response } = await client.POST('/users/avatar', { body } as any);

  if (response.ok) return { success: true };
  return { success: false, message: 'Failed to upload avatar.' };
}

export async function removeFriendAction(
  friendId: string,
): Promise<ActionResult> {
  const { response } = await client.DELETE('/friends/{friendId}', {
    params: { path: { friendId } },
  });

  if (response.ok) return { success: true };
  return { success: false, message: 'Failed to remove friend.' };
}

export async function addFriendAction(friendId: string): Promise<ActionResult> {
  const { response } = await client.POST('/friends/{friendId}', {
    params: { path: { friendId } },
  });

  if (response.ok) return { success: true };
  return { success: false, message: 'Failed to add friend.' };
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
    return { success: false, message: 'Failed to search users.' };
  }

  const results = (data.content ?? []).map((user) => ({
    id: user.id?.val ?? '',
    displayName: user.displayName ?? 'Unknown User',
    avatarId: user.avatarId?.val,
  }));

  return { success: true, results, hasMore: !(data.last ?? true) };
}
