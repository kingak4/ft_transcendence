import BackButton from './BackButton';

// The logged-out counterpart of AppShell: same surface and the same `main`
// scroll container, so a page renders identically inside either, but no rail -
// every Sidebar entry needs a session. The only way out is BackButton.
//
// Padding matches AppShell's horizontally (16px narrow, 56px wide). The narrow
// top is 24px instead of 80px: those 80px are reserved for the hamburger
// button, and there is no hamburger here.
export default function PublicShell({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="bg-surface text-on-surface flex h-full flex-col min-h-0">
      <main className="flex flex-1 flex-col min-h-0 overflow-y-auto px-4 py-6 lg:px-14 lg:py-12">
        {/* Same 720px column as the legal card, so the button lines up with
          the card's left edge instead of the viewport's. */}
        <div className="mx-auto mb-4 flex w-full max-w-[720px]">
          <BackButton />
        </div>
        {children}
      </main>
    </div>
  );
}
