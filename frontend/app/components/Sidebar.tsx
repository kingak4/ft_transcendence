'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

const navItems = [
  { label: 'Home', href: '/home' },
  { label: 'Planner', href: '/planner' },
  { label: 'Dashboard', href: '/dashboard' },
  { label: 'User', href: '/user' },
  { label: 'Settings', href: '/settings' },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="flex h-screen w-44 shrink-0 flex-col bg-white px-3 py-6 shadow-sm">
      <span className="mb-8 px-3 text-xl font-bold italic">42Hub</span>
      <nav className="flex flex-col gap-1">
        {navItems.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-brand-secondary-color text-brand-reversed-main-color'
                  : 'text-brand-reversed-main-color hover:bg-brand-main-color'
              }`}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
