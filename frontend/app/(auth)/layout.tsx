export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="relative min-h-screen">
      <span className="absolute left-6 top-6 text-xl font-bold italic text-white">
        42hub
      </span>
      {children}
    </div>
  );
}
