export const LOCALES = ['en', 'pl', 'fr', 'ar'] as const;

export type Locale = (typeof LOCALES)[number];

export const DEFAULT_LOCALE: Locale = 'en';

export const LOCALE_COOKIE = 'NEXT_LOCALE';

// Endonyms: each language is named in itself, so a reader can always find their own.
export const LOCALE_LABELS: Record<Locale, string> = {
  en: 'English',
  pl: 'Polski',
  fr: 'Français',
  ar: 'العربية',
};

const RTL_LOCALES: ReadonlySet<Locale> = new Set<Locale>(['ar']);

export function isLocale(value: unknown): value is Locale {
  return (
    typeof value === 'string' && (LOCALES as readonly string[]).includes(value)
  );
}

export function getDirection(locale: Locale): 'ltr' | 'rtl' {
  return RTL_LOCALES.has(locale) ? 'rtl' : 'ltr';
}
