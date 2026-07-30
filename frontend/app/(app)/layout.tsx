import { cookies } from 'next/headers';

import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';

export default async function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="bg-surface text-on-surface flex min-h-screen">
      <Sidebar userId={userId} />
      <div className="flex flex-1 flex-col">
        <main className="flex-1 p-8">{children}</main>
        <Footer />
      </div>
    </div>
  );
}
