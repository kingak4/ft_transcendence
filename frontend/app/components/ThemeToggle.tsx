'use client';

import { useTheme, type Theme } from '../hooks/useTheme';
import { THEME_ORDER } from '../lib/theme';

const THEME_LABEL: Record<Theme, string> = {
  brand: '42Hub',
  mocha: 'Mocha',
  latte: 'Latte',
};

// Geometry and typography follow the export's `langBtnStyle` (42Hub.dc.html):
// 6px/10px padding, 9px radius, 11.5px at weight 700. Radius and size snap to
// the nearest fixed Tailwind step (8px, 12px) while padding is exact, because
// spacing is a dynamic scale and the other two are not - see the three rounding
// rules in MIGRATION-INVENTORY.md 12.0.
//
// Krok 6 unit 6.8 rebuilt this as a THREE-OPTION control rather than a cycle,
// which is the shape `langBtnStyle` actually describes: the export parameterises
// it by `active`, so it draws a set with one member filled, not a single button
// whose label reports the current state. Three consequences, all wanted:
//
//  1. The temporary border is gone. 12.5 recorded it as a stand-in, correct only
//     because a lone `bg-surface` button on a `surface` background had nothing
//     to distinguish it. A filled active option supplies that contrast itself,
//     so this stops being the only bordered control in the app.
//  2. The active option takes `bg-hub-cta text-hub-ink` - the same treatment the
//     rail gives its active nav item. Reusing the app's existing "this one is
//     current" vocabulary is the point; inventing a second one would be the
//     systemic conflict §8.2 warns about.
//  3. §14.1's shell-variant axis (`langBtnStyle(active, isSidebar)`) is no longer
//     needed. This component renders under BareLayout's dark gradient AND under
//     the light (app) shell, and the inactive options solve that the way Footer
//     already does - inherit the shell's own colour and modulate it with opacity
//     rather than naming one that can only be right on one of them.
//
// Each theme is now reachable in one click instead of up to two, and a screen
// reader gets a labelled group of pressed/unpressed buttons rather than one
// control whose effect it cannot describe.
export default function ThemeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    // `group` rather than `radiogroup`: these apply immediately on click, they
    // are not a pending choice inside a form, and `aria-pressed` already carries
    // the state. A radiogroup would also promise arrow-key navigation that this
    // does not implement.
    <div
      role="group"
      aria-label="Theme"
      className="border-current/15 flex items-center gap-0.5 rounded-xl border p-0.5"
    >
      {THEME_ORDER.map((option) => {
        const isActive = option === theme;
        return (
          <button
            key={option}
            type="button"
            onClick={() => setTheme(option)}
            aria-pressed={isActive}
            className={`rounded-lg px-2.5 py-1.5 text-xs font-bold transition-opacity ${
              isActive
                ? 'bg-hub-cta text-hub-ink'
                : 'opacity-60 hover:opacity-100'
            }`}
          >
            {THEME_LABEL[option]}
          </button>
        );
      })}
    </div>
  );
}
