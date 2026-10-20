import Footer from './Footer';
import Sidebar from './Sidebar';
import ConnectionBanner from './ConnectionBanner';
import StompProvider from './StompProvider';

export default function AppShell({
  userId,
  children,
}: {
  userId: string;
  children: React.ReactNode;
}) {
  return (
    <StompProvider>
      <div className="bg-surface text-on-surface flex h-full flex-col min-h-0">
        <ConnectionBanner />
        <div className="flex flex-1 min-h-0">
          <Sidebar userId={userId} />
          <div className="flex flex-1 flex-col min-h-0">
            <main
              id="main"
              tabIndex={0}
              className="flex flex-1 flex-col min-h-0 overflow-y-auto px-4 pb-6 pt-20 lg:px-14 lg:pb-12 lg:pt-12 focus-visible:-outline-offset-2"
            >
              {children}
            </main>
            <Footer />
          </div>
        </div>
      </div>
    </StompProvider>
  );
}
