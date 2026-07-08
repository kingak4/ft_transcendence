import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import Script from 'next/script';
import './globals.css';
import StompProvider from './components/StompProvider';

const themeInitScript = `
  (function () {
    try {
      var stored = localStorage.getItem('theme');
      if (stored === 'mocha' || stored === 'latte') {
        document.documentElement.classList.add(stored);
      }
    } catch (e) {}
  })();
`;

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
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
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
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
