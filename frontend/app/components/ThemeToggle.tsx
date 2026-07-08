'use client';

import { useTheme, type Theme } from '../hooks/useTheme';

const THEME_LABEL: Record<Theme, string> = {
  brand: '42Hub',
  mocha: 'Mocha',
  latte: 'Latte',
};

export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label="Cycle color theme"
      className="rounded-md border border-accent bg-surface px-3 py-1.5 text-sm text-on-surface transition-colors hover:opacity-80"
    >
      Theme: {THEME_LABEL[theme]}
    </button>
  );
}
