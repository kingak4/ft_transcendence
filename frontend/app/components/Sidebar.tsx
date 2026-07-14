'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

import BrandLink from './BrandLink';

const navItems = [
  { label: 'Terms of service', href: '/terms-of-service' },
  { label: 'Privacy policy', href: '/privacy-policy' },
  { label: 'Dev: STOMP WebSocket Test', href: '/home' },
];

interface Props {
  userId: string | null;
}

export default function Sidebar({ userId }: Props) {
  const pathname = usePathname();

  return (
    <aside className="flex h-screen w-52 shrink-0 flex-col bg-white px-3 py-6 shadow-sm">
      <BrandLink className="text-brand-reversed-main-color mb-3 px-3" />

      {userId && (
        <Link
          href={`/${userId}`}
          className={`mb-6 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
            pathname === `/${userId}`
              ? 'bg-brand-secondary-color text-brand-reversed-main-color'
              : 'text-brand-reversed-main-color hover:bg-brand-main-color'
          }`}
        >
          My Profile
        </Link>
      )}

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
