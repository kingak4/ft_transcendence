'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useEffect, useRef, useState } from 'react';

import BrandLink from './BrandLink';

const NAV_BASE_CLASSES =
  'rounded-xl px-3.5 py-3.25 text-sm font-bold transition-colors';

function navLinkClasses(isActive: boolean) {
  const state = isActive
    ? 'bg-cta text-on-cta'
    : 'text-on-shell-muted hover:bg-shell-hover hover:text-on-shell';
  return `${NAV_BASE_CLASSES} ${state}`;
}

function navLinkProps(isActive: boolean) {
  return {
    className: navLinkClasses(isActive),
    'aria-current': isActive ? ('page' as const) : undefined,
  };
}

interface Props {
  userId: string | null;
}

export default function Sidebar({ userId }: Props) {
  const t = useTranslations('nav');
  const pathname = usePathname();
  const [openedOn, setOpenedOn] = useState<string | null>(null);
  const isOpen = openedOn === pathname;
  const close = () => setOpenedOn(null);

  const triggerRef = useRef<HTMLButtonElement>(null);
  const panelRef = useRef<HTMLDivElement>(null);
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

  // The trigger hides itself on open; move focus into the panel so it isn't lost.
  useEffect(() => {
    if (!isOpen) return;
    panelRef.current?.querySelector<HTMLElement>('nav a[href]')?.focus();
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
        aria-label={t('open')}
        aria-expanded={isOpen}
        aria-controls="app-nav"
        className={`bg-shell text-on-shell fixed start-4 top-4 z-50 rounded-xl p-2.5 lg:hidden ${isOpen ? 'hidden' : ''}`}
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
          aria-label={t('close')}
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
        />
      )}

      <div
        id="app-nav"
        ref={panelRef}
        className={`bg-shell fixed inset-y-0 start-0 z-40 flex w-[250px] shrink-0 flex-col overflow-y-auto overscroll-contain px-5 py-7 transition-transform lg:visible lg:static lg:translate-x-0 ${isOpen ? 'visible translate-x-0' : 'invisible -translate-x-full rtl:max-lg:translate-x-full'}`}
      >
        <BrandLink className="text-on-shell mb-5 px-2.5" />

        {userId && (
          <nav className="flex flex-col gap-1.5" aria-label={t('main')}>
            <Link
              href={`/${userId}`}
              onClick={close}
              {...navLinkProps(pathname === `/${userId}`)}
            >
              {t('myProfile')}
            </Link>
            <Link
              href={`/chat`}
              onClick={close}
              {...navLinkProps(pathname === `/chat`)}
            >
              {t('chat')}
            </Link>
            <Link
              href="/friends"
              onClick={close}
              {...navLinkProps(pathname === '/friends')}
            >
              {t('friends')}
            </Link>
          </nav>
        )}
      </div>
    </>
  );
}
