'use client';

import { useLocale, useTranslations } from 'next-intl';
import { useTransition } from 'react';

import { setLocale } from '../lib/i18n/actions';
import { LOCALE_LABELS, LOCALES, type Locale } from '../lib/i18n/config';

export default function LanguageSwitcher() {
  const t = useTranslations('footer');
  const locale = useLocale();
  const [isPending, startTransition] = useTransition();

  function handleSelect(option: Locale) {
    startTransition(() => setLocale(option));
  }

  return (
    <div
      role="group"
      aria-label={t('language')}
      aria-busy={isPending || undefined}
      className="border-current/15 flex items-center gap-0.5 rounded-xl border p-0.5"
    >
      {LOCALES.map((option) => {
        const isActive = option === locale;
        return (
          <button
            key={option}
            type="button"
            lang={option}
            onClick={() => handleSelect(option)}
            aria-pressed={isActive}
            className={`rounded-lg px-2.5 py-1.5 text-xs font-bold transition-colors ${
              isActive
                ? 'bg-cta text-on-cta'
                : 'text-muted hover:text-on-surface'
            }`}
          >
            {LOCALE_LABELS[option]}
          </button>
        );
      })}
    </div>
  );
}
