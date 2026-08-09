'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';

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
  // Open-ness is DERIVED, not stored: we remember which route the drawer was
  // opened on, and it counts as open only while we are still on that route.
  //
  // The obvious version - a boolean plus an effect that resets it when
  // `pathname` changes - is what react-hooks/set-state-in-effect rejects, and
  // rightly: it renders the drawer open over the new page, then schedules a
  // second render to close it. Deriving means the very first render after a
  // navigation already has it closed. It also closes on navigations no link
  // handler would catch, such as the browser back button.
  const [openedOn, setOpenedOn] = useState<string | null>(null);
  const isOpen = openedOn === pathname;
  const close = () => setOpenedOn(null);

  // Escape closes any overlay that traps the eye - the backdrop handles the mouse,
  // this handles the keyboard. Bound only while open so the app is not listening
  // to every keystroke on every (app) route. This effect is fine by the same rule
  // above: it sets state from an event callback, not synchronously in the body.
  useEffect(() => {
    if (!isOpen) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setOpenedOn(null);
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [isOpen]);

  return (
    // Unit 1b: two constructions, written mobile-first. Unprefixed classes are the
    // narrow one - the rail is `fixed` and slid out of frame until opened - and
    // `lg:` restores exactly what was there before (`static`, no transform), so
    // above 1024px this component must not move by a pixel.
    <>
      <button
        type="button"
        onClick={() => setOpenedOn(pathname)}
        aria-label="Open navigation"
        aria-expanded={isOpen}
        aria-controls="app-nav"
        className={`bg-hub-shell fixed left-4 top-4 z-50 rounded-xl p-2.5 text-white lg:hidden ${isOpen ? 'hidden' : ''}`}
      >
        <svg width="20" height="20" viewBox="0 0 20 20" aria-hidden="true">
          <path
            d="M2 5h16M2 10h16M2 15h16"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </button>

      {/* A button rather than a div: it is a real click target, and making it a
          button is what gives it keyboard focus and a label for free. */}
      {isOpen && (
        <button
          type="button"
          onClick={close}
          aria-label="Close navigation"
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
        />
      )}

      {/* 250px is the design's rail width; w-52 (208px) left the nav labels tight. */}
      <aside
        id="app-nav"
        className={`bg-hub-shell fixed inset-y-0 left-0 z-40 flex w-[250px] shrink-0 flex-col px-5 py-7 transition-transform lg:static lg:translate-x-0 ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}
      >
        <BrandLink className="mb-5 px-2.5 text-white" />

        {/* `onClick={close}` on every link covers the one case deriving cannot:
          tapping the link for the route you are already on leaves `pathname`
          unchanged, so the drawer would sit there looking stuck. */}
        {userId && (
          <>
            <Link
              href={`/${userId}`}
              onClick={close}
              className={`mb-1.5 ${navLinkClasses(pathname === `/${userId}`)}`}
            >
              My Profile
            </Link>
            <Link
              href={`/chat`}
              onClick={close}
              className={`mb-1.5 ${navLinkClasses(pathname === `/chat`)}`}
            >
              Chat
            </Link>
            {/* Added by unit 2a. The export reaches its equivalent screen from a
              dashboard tile, not the rail - but we have no Home route, so this
              is currently the only way in (12.4). It is also what makes queue
              position 18 necessary: the rail now carries a fourth link and the
              review done at position 10 no longer describes it. */}
            <Link
              href="/friends"
              onClick={close}
              className={`mb-1.5 ${navLinkClasses(pathname === '/friends')}`}
            >
              Friends
            </Link>
          </>
        )}

        <nav className="flex flex-col gap-1.5">
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              onClick={close}
              className={navLinkClasses(pathname === item.href)}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>
    </>
  );
}
