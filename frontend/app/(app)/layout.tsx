import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { client } from '../lib/api-clients';

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

  const { response } = await client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  });

  if (response.status === 401 || response.status === 403 || response.status === 404) {
    redirect('/api/auth/logout');
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
