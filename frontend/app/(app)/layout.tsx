import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { assertValidSession } from '../lib/session';

import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import StompProvider from '../components/StompProvider';

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

  return (
    <StompProvider>
      <div className="bg-surface text-on-surface flex min-h-screen">
        <Sidebar userId={userId} />
        <div className="flex flex-1 flex-col">
          <main className="flex-1 p-8">{children}</main>
          <Footer />
        </div>
      </div>
    </StompProvider>
  );
}
