import { cookies } from 'next/headers';

import { assertValidSession } from '../lib/session';

import AppShell from '../components/AppShell';
import PublicShell from '../components/PublicShell';

// Public pages that a logged-in user should still see inside the app. Unlike
// (app)/layout.tsx this never redirects: a missing or stale session just means
// the logged-out shell. The cookie is checked first so an anonymous visit never
// pays for the session round-trip.
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
