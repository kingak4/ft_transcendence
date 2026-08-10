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

// Friend and user-search actions moved to app/(app)/friends/actions.ts at
// unit 2a. What is left here is what the profile itself does: name and avatar.
