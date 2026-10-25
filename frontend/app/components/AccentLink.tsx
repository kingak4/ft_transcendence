import Link from 'next/link';
import type { ComponentProps } from 'react';

const ACCENT_LINK_CLASSES =
  'text-primary font-semibold transition hover:brightness-125';

type AccentLinkProps = ComponentProps<typeof Link>;

export default function AccentLink({
  className = '',
  ...linkProps
}: AccentLinkProps) {
  return (
    <Link {...linkProps} className={`${ACCENT_LINK_CLASSES} ${className}`} />
  );
}
