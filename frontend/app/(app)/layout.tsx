import { cookies } from 'next/headers';

import Sidebar from '../components/Sidebar';

export default async function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="bg-brand-main-color flex min-h-screen">
      <Sidebar userId={userId} />
      <main className="flex-1 overflow-y-auto p-8">{children}</main>
    </div>
  );
}
