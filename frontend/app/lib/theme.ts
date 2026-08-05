/**
 * Single source of truth for the theme flavours.
 *
 * Deliberately free of 'use client' so both the client-side `useTheme` hook and
 * the server-rendered root layout (which inlines a blocking init script) can
 * import it. Adding a flavour means editing THEME_ORDER and nothing else.
 */

export const THEME_STORAGE_KEY = 'theme';

export const THEME_ORDER = ['brand', 'mocha', 'latte'] as const;

export type Theme = (typeof THEME_ORDER)[number];

/** The flavour rendered by the stylesheet's base layer, i.e. no class on <html>. */
export const DEFAULT_THEME: Theme = 'brand';

/** Flavours that are applied as a class on <html>; every theme except the default. */
export const THEME_CLASSES = THEME_ORDER.filter(
  (theme) => theme !== DEFAULT_THEME,
);
