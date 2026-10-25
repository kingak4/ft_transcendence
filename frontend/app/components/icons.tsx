import type { ReactNode } from 'react';

export type IconName =
  | 'send'
  | 'check'
  | 'clock'
  | 'alert'
  | 'trash'
  | 'chevron-left'
  | 'arrow-left';

// Stroke-only shapes on a 24x24 grid. Icon.tsx supplies the <svg> wrapper,
// so every entry inherits the same stroke width, caps and currentColor.
export const ICON_PATHS: Record<IconName, ReactNode> = {
  send: (
    <>
      <path d="M22 2 11 13" />
      <path d="M22 2 15 22 11 13 2 9 22 2" />
    </>
  ),
  check: <path d="M20 6 9 17l-5-5" />,
  clock: (
    <>
      <circle cx="12" cy="12" r="10" />
      <path d="M12 6v6l4 2" />
    </>
  ),
  alert: (
    <>
      <circle cx="12" cy="12" r="10" />
      <path d="M12 8v4" />
      <path d="M12 16h.01" />
    </>
  ),
  trash: (
    <>
      <path d="M3 6h18" />
      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
    </>
  ),
  'chevron-left': <path d="m15 18-6-6 6-6" />,
  'arrow-left': (
    <>
      <path d="M19 12H5" />
      <path d="m12 19-7-7 7-7" />
    </>
  ),
};
