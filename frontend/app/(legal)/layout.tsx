import { cookies } from 'next/headers';

import { assertValidSession } from '../lib/session';

import AppShell from '../components/AppShell';
import PublicShell from '../components/PublicShell';

export default async function LegalLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  if (userId && (await assertValidSession())) {
    return <AppShell userId={userId}>{children}</AppShell>;
  }

  return <PublicShell>{children}</PublicShell>;
}
