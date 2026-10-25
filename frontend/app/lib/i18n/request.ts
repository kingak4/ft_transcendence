import { getRequestConfig } from 'next-intl/server';
import { cookies, headers } from 'next/headers';

import { LOCALE_COOKIE } from './config';
import { MESSAGES } from './messages';
import { negotiateLocale } from './negotiate';

export default getRequestConfig(async () => {
  const cookieStore = await cookies();
  const headerList = await headers();

  const locale = negotiateLocale(
    cookieStore.get(LOCALE_COOKIE)?.value,
    headerList.get('accept-language'),
  );

  return { locale, messages: MESSAGES[locale] };
});
