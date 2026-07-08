'use client';

import { useCallback, useEffect, useState } from 'react';

export type Theme = 'brand' | 'mocha' | 'latte';

const STORAGE_KEY = 'theme';
const THEME_ORDER: Theme[] = ['brand', 'mocha', 'latte'];

function isTheme(value: string | null): value is Theme {
  return value === 'brand' || value === 'mocha' || value === 'latte';
}

function applyTheme(theme: Theme) {
  document.documentElement.classList.remove('mocha', 'latte');
  if (theme !== 'brand') {
    document.documentElement.classList.add(theme);
  }
}

export function useTheme() {
  const [theme, setTheme] = useState<Theme>('brand');

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (isTheme(stored)) {
      setTheme(stored);
    }
  }, []);

  useEffect(() => {
    applyTheme(theme);
    localStorage.setItem(STORAGE_KEY, theme);
  }, [theme]);

  const toggleTheme = useCallback(() => {
    setTheme((prev) => THEME_ORDER[(THEME_ORDER.indexOf(prev) + 1) % THEME_ORDER.length]);
  }, []);

  return { theme, toggleTheme };
}
