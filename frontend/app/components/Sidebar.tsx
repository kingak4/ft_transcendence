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

// `navItemStyle` in the export: 12px radius, 13x14px padding, 14.5px at weight
// 700. Radius and size take their dictionary rows (12.0: "pozycja nawigacji" and
// "tekst interfejsu"); the padding is written exactly, because Tailwind 4's
// spacing scale is dynamic and 13px needs no rounding.
const NAV_BASE_CLASSES =
  'rounded-xl px-3.5 py-3.25 text-sm font-bold transition-colors';

/**
 * TODO(design-migration): this rail already renders in the new design on every
 * (app) page, so /[userId], /terms-of-service, /privacy-policy and /stomp show
 * a dark sidebar beside old-palette content. That drift is deliberate and
 * temporary (PLAN-static-chat-page.md section 2.7) - closing it means
 * restyling those pages, not reverting this.
 *
 * The three on-rail tokens are no longer placeholders. Step 4 confirmed all
 * three against the export (MIGRATION-INVENTORY.md 12.4, 2026-08-09):
 * hub-on-shell is #ffffff exactly; hub-on-shell-muted stays at 0.7 where the
 * export says 0.75, because 0.05 of alpha is below the threshold of visibility
 * and churning the token layer costs more than it buys; hub-shell-hover has no
 * counterpart at all, since the export is static and draws no hover state.
 *
 * Still open, and deliberately not a Step 4 concern: these tokens carry no
 * .mocha/.latte override, so the rail stays a fixed dark column whatever
 * ThemeToggle says. That is Step 5's job, not a styling defect.
 *
 * The rail is a fixed dark gradient, so the text on it is fixed too - the same
 * reasoning that keeps the hub brand hues out of theme indirection. Extracted
 * because both the profile link and the nav items need the identical pair,
 * mirroring `buttonClasses()` in Button.tsx.
 */
function navLinkClasses(isActive: boolean) {
  const state = isActive
    ? 'bg-hub-cta text-hub-ink'
    : 'text-hub-on-shell-muted hover:bg-hub-shell-hover hover:text-hub-on-shell';
  return `${NAV_BASE_CLASSES} ${state}`;
}

interface Props {
  userId: string | null;
}

export default function Sidebar({ userId }: Props) {
  const pathname = usePathname();

  return (
    // 250px is the design's rail width; w-52 (208px) left the nav labels tight.
    <aside className="bg-hub-shell flex w-[250px] shrink-0 flex-col px-5 py-7">
      <BrandLink className="mb-5 px-2.5 text-white" />

      {userId && (
        <>
          <Link
            href={`/${userId}`}
            className={`mb-1.5 ${navLinkClasses(pathname === `/${userId}`)}`}
          >
            My Profile
          </Link>
          <Link
            href={`/chat`}
            className={`mb-1.5 ${navLinkClasses(pathname === `/chat`)}`}
          >
            Chat
          </Link>
        </>
      )}

      <nav className="flex flex-col gap-1.5">
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
