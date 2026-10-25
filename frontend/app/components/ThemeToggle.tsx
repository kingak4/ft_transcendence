'use client';

import { useTranslations } from 'next-intl';

import { useTheme, type Theme } from '../hooks/useTheme';
import { THEME_ORDER } from '../lib/theme';

const THEME_LABEL: Record<Theme, string> = {
  brand: '42Hub',
  mocha: 'Mocha',
  latte: 'Latte',
};

export default function ThemeToggle() {
  const t = useTranslations('footer');
  const { theme, setTheme } = useTheme();

  return (
    <div
      role="group"
      aria-label={t('theme')}
      className="border-current/15 flex items-center gap-0.5 rounded-xl border p-0.5"
    >
      {THEME_ORDER.map((option) => {
        const isActive = option === theme;
        return (
          <button
            key={option}
            type="button"
            onClick={() => setTheme(option)}
            aria-pressed={isActive}
            className={`rounded-lg px-2.5 py-1.5 text-xs font-bold transition-colors ${
              isActive
                ? 'bg-cta text-on-cta'
                : 'text-muted hover:text-on-surface'
            }`}
          >
            {THEME_LABEL[option]}
          </button>
        );
      })}
    </div>
  );
}
