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
      className="border-primary bg-surface text-on-surface rounded-md border px-3 py-1.5 text-sm transition-colors hover:opacity-80"
    >
      Theme: {THEME_LABEL[theme]}
    </button>
  );
}
