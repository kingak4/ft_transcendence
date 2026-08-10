'use server';

import { client } from '../../lib/api-clients';

// Declared here rather than shared with the profile's actions file. A grep at
// unit 2a showed nothing imports `ActionResult` across a module boundary -
// `useAsyncAction` declares its own structurally identical `AsyncActionResult`
// - so this is file vocabulary, not a contract two routes have to agree on.
// TypeScript is structurally typed, so the buttons accept either.
//
// Krok 6 unit 6.11 listed this as duplication to collapse and CLOSED IT AS
// INTENTIONAL instead. The step's subject is components that describe the same
// appearance twice; two files each naming their own local result shape is not
// that. Extracting it would create the cross-module dependency the note above
// says does not exist, and the third copy in `useAsyncAction` shows the shape is
// a convention rather than a type anyone shares. Recorded in 14.2.
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
