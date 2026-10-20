import Link from 'next/link';

export default function BackButton() {
  return (
    <Link
      href="/"
      className="text-on-surface self-start text-sm font-semibold hover:underline"
    >
      ← Back
    </Link>
  );
}
