import Link from 'next/link';

import ThemeToggle from './ThemeToggle';

const FOOTER_LINKS = [
  {
    href: 'https://github.com/kingak4/ft_transcendence',
    label: 'GitHub',
    external: true,
  },
  { href: '/privacy-policy', label: 'Privacy Policy' },
  { href: '/terms-of-service', label: 'Terms of Service' },
  { href: '/api/docs', label: 'API' },
];

export default function Footer() {
  return (
    <footer className="border-current/10 mt-auto flex flex-wrap items-center justify-between gap-4 border-t px-4 py-4 text-xs lg:px-14">
      <nav className="flex flex-wrap gap-5" aria-label="Footer">
        {FOOTER_LINKS.map((link) =>
          link.external ? (
            <a
              key={link.href}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className="font-semibold text-muted transition-colors hover:text-on-surface"
            >
              {link.label}
            </a>
          ) : (
            <Link
              key={link.href}
              href={link.href}
              className="font-semibold text-muted transition-colors hover:text-on-surface"
            >
              {link.label}
            </Link>
          ),
        )}
      </nav>
      <ThemeToggle />
    </footer>
  );
}
