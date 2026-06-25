'use server';

import { client } from '../../lib/api-clients';

type ActionResult = { success: true } | { success: false; message: string };

export async function updateDisplayNameAction(displayName: string): Promise<ActionResult> {
  const { response } = await client.PATCH('/users/display-name', {
    body: { displayName },
  });

  if (response.ok) return { success: true };
  return { success: false, message: 'Failed to update display name. Try a different one.' };
}

export async function uploadAvatarAction(formData: FormData): Promise<ActionResult> {
  const { cookies } = await import('next/headers');
  const cookieStore = await cookies();
  const token = cookieStore.get('auth_token')?.value;

  const file = formData.get('file') as File;
  const body = new FormData();
  body.append('file', file);

  const response = await fetch(`${process.env.BACKEND_URL}/users/avatar`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body,
  });

  if (response.ok) return { success: true };
  return { success: false, message: 'Failed to upload avatar.' };
}
