import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { assertValidSession } from '../lib/session';

import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import ConnectionBanner from '../components/ConnectionBanner';
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
        <ConnectionBanner />
        <div className="flex flex-1"> {/*TODO: CHECK THE VISUAL AFTER REBASE*/}
          <Sidebar userId={userId} />
          <div className="flex flex-1 flex-col">
            {/* Page padding for app routes, from the dictionary (12.0): 48/56px
              on wide screens, 24/16px narrow - two 56px gutters take a third of
              a 360px viewport. Was a flat p-8 (32px) at both widths.
              
              The narrow top padding is 80px rather than 24px because unit 1b
              puts a fixed hamburger button in that corner. It is reserved space,
              not a spacing decision, which is why it disappears at `lg:` where
              the rail is a column again and nothing overlaps the content. */}
            <main className="flex-1 px-4 pt-20 pb-6 lg:px-14 lg:pt-12 lg:pb-12">
              {children}
            </main>
            <Footer />
          </div>
        </div>
      </div>
    </StompProvider>
  );
}
