export const THEME_STORAGE_KEY = 'theme';

export const THEME_ORDER = ['brand', 'mocha', 'latte'] as const;

export type Theme = (typeof THEME_ORDER)[number];

export const DEFAULT_THEME: Theme = 'brand';

export const THEME_CLASSES = THEME_ORDER.filter(
  (theme) => theme !== DEFAULT_THEME,
);
