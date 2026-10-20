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

function readAppliedTheme(): Theme {
  const { classList } = document.documentElement;
  return (
    THEME_CLASSES.find((theme) => classList.contains(theme)) ?? DEFAULT_THEME
  );
}

function readServerTheme(): Theme {
  return DEFAULT_THEME;
}

export function useTheme() {
  const theme = useSyncExternalStore(
    subscribe,
    readAppliedTheme,
    readServerTheme,
  );

  function setTheme(next: Theme) {
    applyTheme(next);
    localStorage.setItem(THEME_STORAGE_KEY, next);
    emitChange();
  }

  function toggleTheme() {
    const current = readAppliedTheme();
    setTheme(THEME_ORDER[(THEME_ORDER.indexOf(current) + 1) % THEME_ORDER.length]);
  }

  return { theme, setTheme, toggleTheme };
}
