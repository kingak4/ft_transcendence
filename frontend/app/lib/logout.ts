'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export async function clearAuthCookies() {
  const cookieStore = await cookies();
  cookieStore.delete('auth_token');
  cookieStore.delete('user_id');
}

export async function logout() {
  await clearAuthCookies();
  redirect('/login');
}

// Clears the session without navigating away — use when the current page
// should re-render in-place rather than redirect to login.
export async function clearSession() {
  await clearAuthCookies();
}
