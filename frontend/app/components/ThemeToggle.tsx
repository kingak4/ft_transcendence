'use client';

import { useTheme, type Theme } from '../hooks/useTheme';

const THEME_LABEL: Record<Theme, string> = {
  brand: '42Hub',
  mocha: 'Mocha',
  latte: 'Latte',
};

// Geometry and typography follow the export's `langBtnStyle` (42Hub.dc.html):
// 6px/10px padding, 9px radius, 11.5px at weight 700. That is the export's only
// small chrome-level control that cycles a global setting, which is exactly what
// this is. Radius and size snap to the nearest fixed Tailwind step (8px, 12px)
// while padding is exact, because spacing is a dynamic scale and the other two
// are not - see the three rounding rules in MIGRATION-INVENTORY.md 12.0.
//
// The border does NOT follow the export, and is TEMPORARY. Every button in the
// export is `border: none` - they are legible because they sit on a dark
// gradient sidebar. This one still lives in `Footer`, on a background that is
// the same `surface` token as its own fill, so without the border there is
// nothing to see. Once the toggle is relocated to where the new design puts it,
// that context supplies the contrast and the border must come off; keeping it
// then would make this the only bordered button in the app. Tracked in
// MIGRATION-INVENTORY.md 12.5 - do not read this as a settled deviation.
//
// `transition-colors` was doing nothing here - it does not cover `opacity`, so
// the hover was snapping rather than easing.
export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label={`Theme: ${THEME_LABEL[theme]}. Activate to cycle.`}
      className="border-primary bg-surface text-on-surface rounded-lg border px-2.5 py-1.5 text-xs font-bold transition-opacity hover:opacity-80"
    >
      Theme: {THEME_LABEL[theme]}
    </button>
  );
}
