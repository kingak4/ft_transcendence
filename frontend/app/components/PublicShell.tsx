export default function PublicShell({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="bg-surface text-on-surface flex h-full flex-col min-h-0">
      <main className="flex flex-1 flex-col min-h-0 overflow-y-auto px-4 py-6 lg:px-14 lg:py-12">
        {children}
      </main>
    </div>
  );
}
