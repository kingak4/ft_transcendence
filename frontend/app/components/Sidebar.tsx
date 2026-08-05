'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

import BrandLink from './BrandLink';

// TODO(stomp): /chat is not listed here, so the page is reachable only by
// typing the URL. Add `{ label: 'Chat', href: '/chat' }` when the route stops
// being a static preview - left out deliberately while it shows fixtures.
const navItems = [
  { label: 'Terms of service', href: '/terms-of-service' },
  { label: 'Privacy policy', href: '/privacy-policy' },
  { label: 'Dev: STOMP WebSocket Test', href: '/stomp' },
];

const NAV_BASE_CLASSES =
  'rounded-lg px-3 py-2 text-sm font-medium transition-colors';

/**
 * TODO(design-migration): this rail already renders in the new design on every
 * (app) page, so /[userId], /terms-of-service, /privacy-policy and /stomp show
 * a dark sidebar beside old-palette content. That drift is deliberate and
 * temporary (PLAN-static-chat-page.md section 2.7) - closing it means
 * restyling those pages, not reverting this.
 *
 * TODO(design-migration): the hardcoded white/70 below is a stand-in. Once
 * hub-* carries theme overrides, replace it with a token for on-rail text so
 * the sidebar responds to ThemeToggle like everything else.
 *
 * The rail is a fixed dark gradient, so the text on it is fixed too - the same
 * reasoning that keeps the hub brand hues out of theme indirection. Extracted
 * because both the profile link and the nav items need the identical pair,
 * mirroring `buttonClasses()` in Button.tsx.
 */
function navLinkClasses(isActive: boolean) {
  const state = isActive
    ? 'bg-hub-cta text-hub-ink'
    : 'text-white/70 hover:bg-white/10 hover:text-white';
  return `${NAV_BASE_CLASSES} ${state}`;
}

interface Props {
  userId: string | null;
}

export default function Sidebar({ userId }: Props) {
  const pathname = usePathname();

  return (
    // 250px is the design's rail width; w-52 (208px) left the nav labels tight.
    <aside className="bg-hub-shell flex w-[250px] shrink-0 flex-col px-3 py-6 shadow-sm">
      <BrandLink className="mb-3 px-3 text-white" />

      {userId && (
        <Link
          href={`/${userId}`}
          className={`mb-6 ${navLinkClasses(pathname === `/${userId}`)}`}
        >
          My Profile
        </Link>
      )}

      <nav className="flex flex-col gap-1">
        {navItems.map((item) => (
          <Link
            key={item.href}
            href={item.href}
            className={navLinkClasses(pathname === item.href)}
          >
            {item.label}
          </Link>
        ))}
      </nav>
    </aside>
  );
}
