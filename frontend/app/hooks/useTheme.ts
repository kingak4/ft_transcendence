'use client';

import { useSyncExternalStore } from 'react';

import {
  DEFAULT_THEME,
  THEME_CLASSES,
  THEME_ORDER,
  THEME_STORAGE_KEY,
  type Theme,
} from '../lib/theme';

export type { Theme };

/**
 * The applied flavour lives on <html>, not in React state: the blocking init
 * script in the root layout writes it before first paint. So the DOM is the
 * store and this hook subscribes to it, rather than mirroring it into useState
 * and reconciling after mount (which is the cascading render that
 * react-hooks/set-state-in-effect flags).
 *
 * Module scope, not hook scope: React re-subscribes whenever `subscribe`
 * changes identity, and one DOM means one store to begin with.
 */
const listeners = new Set<() => void>();

function subscribe(onStoreChange: () => void) {
  listeners.add(onStoreChange);
  return () => {
    listeners.delete(onStoreChange);
  };
}

function emitChange() {
  listeners.forEach((notify) => notify());
}

function applyTheme(theme: Theme) {
  const { classList } = document.documentElement;
  classList.remove(...THEME_CLASSES);
  if (theme !== DEFAULT_THEME) {
    classList.add(theme);
  }
}

/**
 * getSnapshot. Must return a value that is stable under Object.is between
 * unchanged reads — a Theme is a string from a fixed set, so it is. Returning
 * a fresh object here would loop React forever.
 */
function readAppliedTheme(): Theme {
  const { classList } = document.documentElement;
  return (
    THEME_CLASSES.find((theme) => classList.contains(theme)) ?? DEFAULT_THEME
  );
}

/**
 * getServerSnapshot. No class is on <html> during SSR, so the server and the
 * first client render agree on DEFAULT_THEME; React then re-reads the real
 * snapshot after hydration. This makes the anti-mismatch intent explicit
 * instead of leaving it implied by a useState initial value.
 */
function readServerTheme(): Theme {
  return DEFAULT_THEME;
}

export function useTheme() {
  const theme = useSyncExternalStore(
    subscribe,
    readAppliedTheme,
    readServerTheme,
  );

  function toggleTheme() {
    const current = readAppliedTheme();
    const next =
      THEME_ORDER[(THEME_ORDER.indexOf(current) + 1) % THEME_ORDER.length];
    applyTheme(next);
    localStorage.setItem(THEME_STORAGE_KEY, next);
    emitChange();
  }

  return { theme, toggleTheme };
}
