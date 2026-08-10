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

// Geometry follows the "padding nagłówka i stopki" row of the dictionary
// (MIGRATION-INVENTORY.md 12.0): 16px vertical, 56px horizontal, narrowing to
// 16px horizontal on small screens because two 56px gutters eat a third of a
// 360px viewport. Links are the "etykieta mała" row - 12px at 600.
//
// `border-primary/20` was not any row of the dictionary. The export's footer
// edge is the raised-surface boundary, which 12.4 resolved to elevated-border.
//
// Colour is INHERITED and modulated, not named. Position 9 used `on-surface/60`
// with a matching hover, which was correct while every route was light. Position
// 21 put `(marketing)` and `(auth)` on a dark gradient while `(app)` stayed
// light, and this one component renders on both - a named colour can only be
// right on one of them.
//
// So the footer takes whatever its shell sets (white under BareLayout,
// on-surface under (app)) and expresses "muted" as opacity instead. Same for the
// rule above it: `border-current/10` resolves to white/10 on the dark shell,
// close to the export's rgba(255,255,255,0.12), and to a faint dark line on the
// light one, close to its #e3ebe6. The alternative was a variant prop, which is
// an API change (§8.7 reguła 3) and Step 6's business.
//
// Not `hub-time` either, even though --theme-hub-time is #9aa5a0, the export's
// exact value: it is a fixed light-mode grey with no .mocha/.latte override, so
// it would ignore ThemeToggle and fail §8.9 pkt 5.
//
// What is deliberately NOT fixed here: on (app) routes this footer has no
// counterpart in the export at all - those links live at the bottom of the
// sidebar, and GitHub is a dashboard card, not chrome. Both are structural, not
// styling. See 12.5.
export default function Footer() {
  return (
    <footer className="border-current/10 mt-auto flex flex-wrap items-center justify-between gap-4 border-t px-4 py-4 text-xs lg:px-14">
      <nav className="flex flex-wrap gap-5">
        {FOOTER_LINKS.map((link) =>
          link.external ? (
            <a
              key={link.href}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className="font-semibold opacity-60 transition-opacity hover:opacity-100"
            >
              {link.label}
            </a>
          ) : (
            <Link
              key={link.href}
              href={link.href}
              className="font-semibold opacity-60 transition-opacity hover:opacity-100"
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
