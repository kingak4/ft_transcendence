'use server';

import { cookies } from 'next/headers';

import { isLocale, LOCALE_COOKIE } from './config';

const ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

// Setting a cookie in a Server Action makes Next.js re-render the current
// route, so the page comes back in the new language without a reload.
export async function setLocale(locale: string) {
  if (!isLocale(locale)) return;

  const cookieStore = await cookies();
  cookieStore.set(LOCALE_COOKIE, locale, {
    maxAge: ONE_YEAR_SECONDS,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax',
    path: '/',
  });
}
