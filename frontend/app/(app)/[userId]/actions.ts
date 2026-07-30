'use server';

import { client } from '../../lib/api-clients';
import type { operations } from '../../types/api';

export type ActionResult =
  | { success: true }
  | { success: false; message: string };

// openapi-typescript models a `multipart/form-data` binary field as a plain
// `{ file: string }` shape (it has no concept of the browser's FormData).
// openapi-fetch's runtime, however, sends a real FormData instance as-is
// when it sees one (see its defaultBodySerializer). The generated type and
// what we actually send are unsound relative to each other, so this cast
// documents *why* rather than papering over it with `any`.
type UploadAvatarBody = NonNullable<
  operations['uploadAvatar']['requestBody']
>['content']['multipart/form-data'];

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

  const { response } = await client.POST('/users/avatar', {
    body: body as unknown as UploadAvatarBody,
  });

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
    id: user.id ?? '',
    displayName: user.displayName ?? 'Unknown User',
    avatarId: user.avatarId?.val,
  }));

  return { success: true, results, hasMore: !(data.last ?? true) };
}
