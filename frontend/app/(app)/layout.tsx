import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { assertValidSession } from '../lib/session';

import AppShell from '../components/AppShell';

export default async function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  if (!userId) {
    redirect('/login');
  }

  const isValidSession = await assertValidSession();

  if (!isValidSession) {
    redirect('/auth/logout');
  }

  return <AppShell userId={userId}>{children}</AppShell>;
}
