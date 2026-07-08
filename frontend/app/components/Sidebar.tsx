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
  const isLandingPage = pathname === '/';

  return (
    <aside className="bg-surface flex w-52 shrink-0 flex-col px-3 py-6 shadow-sm">
      <BrandLink className="text-on-surface mb-3 px-3" />

      {!isLandingPage && userId && (
        <Link
          href={`/${userId}`}
          className={`mb-6 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
            pathname === `/${userId}`
              ? 'bg-primary text-on-primary'
              : 'text-on-surface hover:bg-primary/10'
          }`}
        >
          My Profile
        </Link>
      )}

      {!isLandingPage && (
        <nav className="flex flex-col gap-1">
          {navItems.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary text-on-primary'
                    : 'text-on-surface hover:bg-primary/10'
                }`}
              >
                {item.label}
              </Link>
            );
          })}
        </nav>
      )}
    </aside>
  );
}
