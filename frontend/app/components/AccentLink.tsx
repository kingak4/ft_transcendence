import Link from 'next/link';
import type { ComponentProps } from 'react';

// Maps to the export's accent link on the auth card ("Forgot Password?"):
// #a3e635 at weight 600, which is --theme-primary and `font-semibold`. Not the
// export's prose link (#2f9bcf, the global `a` rule) - all three call sites put
// this at the foot of a card, not inside running text. Weight was 700 here.
const ACCENT_LINK_CLASSES =
  'text-primary font-semibold transition-colors hover:brightness-125';

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
