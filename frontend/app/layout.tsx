import type { Metadata } from 'next';
import { NextIntlClientProvider } from 'next-intl';
import { getLocale, getMessages, getTranslations } from 'next-intl/server';
import { Geist_Mono, Manrope, Noto_Sans_Arabic } from 'next/font/google';
import Script from 'next/script';
import './globals.css';
import SkipLink from './components/SkipLink';
import { getDirection } from './lib/i18n/config';
import { THEME_CLASSES, THEME_STORAGE_KEY } from './lib/theme';

const themeInitScript = `
  (function () {
    try {
      var classes = ${JSON.stringify(THEME_CLASSES)};
      var stored = localStorage.getItem(${JSON.stringify(THEME_STORAGE_KEY)});
      if (classes.indexOf(stored) !== -1) {
        document.documentElement.classList.add(stored);
      }
    } catch (e) {}
  })();
`;

const manrope = Manrope({
  variable: '--font-manrope',
  subsets: ['latin', 'latin-ext'],
  preload: false,
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
  preload: false,
});

// Manrope has no Arabic glyphs; globals.css switches to this font under :lang(ar).
const notoSansArabic = Noto_Sans_Arabic({
  variable: '--font-noto-arabic',
  subsets: ['arabic'],
  preload: false,
});

// Only Server Components read these, so they stay out of the client payload.
const SERVER_ONLY_NAMESPACES = ['legal'];

function clientMessages(messages: Awaited<ReturnType<typeof getMessages>>) {
  return Object.fromEntries(
    Object.entries(messages).filter(
      ([namespace]) => !SERVER_ONLY_NAMESPACES.includes(namespace),
    ),
  );
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('metadata');
  return {
    title: {
      default: '42Hub',
      template: '%s · 42Hub',
    },
    description: t('description'),
  };
}

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const locale = await getLocale();
  const messages = await getMessages();

  return (
    <html
      lang={locale}
      dir={getDirection(locale)}
      className={`${manrope.variable} ${geistMono.variable} ${notoSansArabic.variable} h-full antialiased`}
      suppressHydrationWarning
    >
      <head>
        <Script id="theme-init">
          {themeInitScript}
        </Script>
      </head>
      <body className="flex h-full flex-col">
        <NextIntlClientProvider messages={clientMessages(messages)}>
          <SkipLink />
          {children}
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
