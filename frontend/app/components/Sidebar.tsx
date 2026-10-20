'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import BrandLink from './BrandLink';

const NAV_BASE_CLASSES =
  'rounded-xl px-3.5 py-3.25 text-sm font-bold transition-colors';

function navLinkClasses(isActive: boolean) {
  const state = isActive
    ? 'bg-cta text-on-primary'
    : 'text-on-shell-muted hover:bg-shell-hover hover:text-on-shell';
  return `${NAV_BASE_CLASSES} ${state}`;
}

interface Props {
  userId: string | null;
}

export default function Sidebar({ userId }: Props) {
  const pathname = usePathname();
  const [openedOn, setOpenedOn] = useState<string | null>(null);
  const isOpen = openedOn === pathname;
  const close = () => setOpenedOn(null);

  const triggerRef = useRef<HTMLButtonElement>(null);
  const shouldRestoreFocus = useRef(false);

  const closeAndRestoreFocus = () => {
    shouldRestoreFocus.current = true;
    setOpenedOn(null);
  };

  useEffect(() => {
    if (isOpen || !shouldRestoreFocus.current) return;
    shouldRestoreFocus.current = false;
    triggerRef.current?.focus();
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        shouldRestoreFocus.current = true;
        setOpenedOn(null);
      }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [isOpen]);

  return (
    <>
      <button
        type="button"
        ref={triggerRef}
        onClick={() => setOpenedOn(pathname)}
        aria-label="Open navigation"
        aria-expanded={isOpen}
        aria-controls="app-nav"
        className={`bg-shell text-on-shell fixed left-4 top-4 z-50 rounded-xl p-2.5 lg:hidden ${isOpen ? 'hidden' : ''}`}
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

      {isOpen && (
        <button
          type="button"
          onClick={closeAndRestoreFocus}
          aria-label="Close navigation"
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
        />
      )}

      <aside
        id="app-nav"
        className={`bg-shell fixed inset-y-0 left-0 z-40 flex w-[250px] shrink-0 flex-col overflow-y-auto overscroll-contain px-5 py-7 transition-transform lg:visible lg:static lg:translate-x-0 ${isOpen ? 'visible translate-x-0' : 'invisible -translate-x-full'}`}
      >
        <BrandLink className="text-on-shell mb-5 px-2.5" />

        {userId && (
          <nav className="flex flex-col gap-1.5" aria-label="Main">
            <Link
              href={`/${userId}`}
              onClick={close}
              className={navLinkClasses(pathname === `/${userId}`)}
            >
              My Profile
            </Link>
            <Link
              href={`/chat`}
              onClick={close}
              className={navLinkClasses(pathname === `/chat`)}
            >
              Chat
            </Link>
            <Link
              href="/friends"
              onClick={close}
              className={navLinkClasses(pathname === '/friends')}
            >
              Friends
            </Link>
          </nav>
        )}
      </aside>
    </>
  );
}
