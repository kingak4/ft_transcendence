'use client';

import { useCallback, useEffect, useState } from 'react';

import {
  DEFAULT_THEME,
  THEME_CLASSES,
  THEME_ORDER,
  THEME_STORAGE_KEY,
  type Theme,
} from '../lib/theme';

export type { Theme };

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

export function useTheme() {
  const [theme, setTheme] = useState<Theme>(DEFAULT_THEME);

  useEffect(() => {
    setTheme(readAppliedTheme());
  }, []);

  const toggleTheme = useCallback(() => {
    const current = readAppliedTheme();
    const next =
      THEME_ORDER[(THEME_ORDER.indexOf(current) + 1) % THEME_ORDER.length];
    applyTheme(next);
    localStorage.setItem(THEME_STORAGE_KEY, next);
    setTheme(next);
  }, []);

  return { theme, toggleTheme };
}
