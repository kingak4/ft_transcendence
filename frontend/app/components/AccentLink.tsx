import Link from 'next/link';
import type { ComponentProps } from 'react';

const ACCENT_LINK_CLASSES =
  'text-primary font-bold transition-colors hover:brightness-125';

// Takes exactly what `next/link` takes — this component adds styling and
// nothing else, so it should not narrow what callers can pass through.
type AccentLinkProps = ComponentProps<typeof Link>;

export default function AccentLink({
  className = '',
  ...linkProps
}: AccentLinkProps) {
  // As with `TextField`, `className` is a layout escape hatch only.
  return (
    <Link {...linkProps} className={`${ACCENT_LINK_CLASSES} ${className}`} />
  );
}
