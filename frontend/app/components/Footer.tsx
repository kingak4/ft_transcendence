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
];

export default function Footer() {
  return (
    <footer className="border-primary/20 text-on-surface mt-auto flex flex-wrap items-center justify-between gap-4 border-t px-6 py-4 text-sm">
      <nav className="flex flex-wrap gap-4">
        {FOOTER_LINKS.map((link) =>
          link.external ? (
            <a
              key={link.href}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className="hover:text-primary transition-colors"
            >
              {link.label}
            </a>
          ) : (
            <Link
              key={link.href}
              href={link.href}
              className="hover:text-primary transition-colors"
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
