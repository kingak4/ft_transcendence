import Link from 'next/link';
import { useTranslations } from 'next-intl';

import LanguageSwitcher from './LanguageSwitcher';
import ThemeToggle from './ThemeToggle';

type FooterLabel = 'github' | 'privacyPolicy' | 'termsOfService' | 'api';

const FOOTER_LINKS: { href: string; label: FooterLabel; external?: boolean }[] =
  [
    {
      href: 'https://github.com/kingak4/ft_transcendence',
      label: 'github',
      external: true,
    },
    { href: '/privacy-policy', label: 'privacyPolicy' },
    { href: '/terms-of-service', label: 'termsOfService' },
    { href: '/api/docs', label: 'api' },
  ];

type FooterTone = 'surface' | 'start-page';

const TONE_CLASSES: Record<FooterTone, string> = {
  surface: '',
  'start-page': 'footer-on-start-page',
};

export default function Footer({ tone = 'surface' }: { tone?: FooterTone }) {
  const t = useTranslations('footer');

  return (
    <footer
      dir="ltr"
      className={`border-current/10 mt-auto flex flex-wrap items-center justify-between gap-4 border-t px-4 py-4 text-xs lg:px-14 ${TONE_CLASSES[tone]}`}
    >
      <nav className="flex flex-wrap gap-5" aria-label={t('label')}>
        {FOOTER_LINKS.map((link) =>
          link.external ? (
            <a
              key={link.href}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className="font-semibold text-muted transition-colors hover:text-on-surface"
            >
              {t(link.label)}
            </a>
          ) : (
            <Link
              key={link.href}
              href={link.href}
              className="font-semibold text-muted transition-colors hover:text-on-surface"
            >
              {t(link.label)}
            </Link>
          ),
        )}
      </nav>
      <div className="flex flex-wrap items-center gap-3">
        <LanguageSwitcher />
        <ThemeToggle />
      </div>
    </footer>
  );
}
