import type { Metadata } from 'next';
import { Geist_Mono, Manrope, Noto_Sans_Arabic } from 'next/font/google';
import Script from 'next/script';
import './globals.css';
import StompProvider from './components/StompProvider';
import { THEME_CLASSES, THEME_STORAGE_KEY } from './lib/theme';

// Runs before paint to apply the stored flavour, avoiding a flash of the default
// theme. The body is never compiled or type-checked, so keep it plain ES5 and
// inject every theme-specific value from ./lib/theme.
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
  subsets: ['latin'],
  weight: ['400', '500', '600', '700', '800'],
});

const notoSansArabic = Noto_Sans_Arabic({
  variable: '--font-noto-sans-arabic',
  subsets: ['arabic'],
  weight: ['400', '500', '600', '700', '800'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: '42Hub',
  description: 'Every skill has a story — start yours.',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${manrope.variable} ${notoSansArabic.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col">
        <Script
          id="theme-init"
          strategy="beforeInteractive"
          dangerouslySetInnerHTML={{ __html: themeInitScript }}
        />
        <StompProvider>{children}</StompProvider>
      </body>
    </html>
  );
}
