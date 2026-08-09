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

// Geometry follows the "padding nagłówka i stopki" row of the dictionary
// (MIGRATION-INVENTORY.md 12.0): 16px vertical, 56px horizontal, narrowing to
// 16px horizontal on small screens because two 56px gutters eat a third of a
// 360px viewport. Links are the "etykieta mała" row - 12px at 600.
//
// `border-primary/20` was not any row of the dictionary. The export's footer
// edge is the raised-surface boundary, which 12.4 resolved to elevated-border.
//
// The muted link colour is `on-surface/60`, NOT `hub-time` - even though
// `--theme-hub-time` is #9aa5a0, the export's exact value. Two reasons, and the
// second is the binding one: hub-* is being retired in Step 8, and hub-time has
// no .mocha/.latte override, so an exact match here would be a fixed light-grey
// that ignores ThemeToggle and fails §8.9 pkt 5. The semantic token flips with
// the theme, so the *role* stays muted in every flavour. Hover then goes to full
// strength rather than to `primary`, which is the low-contrast lime already
// recorded against AccentLink.
//
// What is deliberately NOT fixed here: on (app) routes this footer has no
// counterpart in the export at all - those links live at the bottom of the
// sidebar, and GitHub is a dashboard card, not chrome. Both are structural, not
// styling. See 12.5.
export default function Footer() {
  return (
    <footer className="border-elevated-border text-on-surface/60 mt-auto flex flex-wrap items-center justify-between gap-4 border-t px-4 py-4 text-xs lg:px-14">
      <nav className="flex flex-wrap gap-5">
        {FOOTER_LINKS.map((link) =>
          link.external ? (
            <a
              key={link.href}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className="font-semibold hover:text-on-surface transition-colors"
            >
              {link.label}
            </a>
          ) : (
            <Link
              key={link.href}
              href={link.href}
              className="font-semibold hover:text-on-surface transition-colors"
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
