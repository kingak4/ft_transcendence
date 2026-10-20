import Link from 'next/link';
import Icon from './Icon';

export default function BackButton() {
  return (
    <Link
      href="/"
      className="text-on-surface inline-flex items-center gap-1 self-start text-sm font-semibold hover:underline"
    >
      <Icon name="arrow-left" size="sm" /> Back
    </Link>
  );
}
