import Link from 'next/link';

// The logged-out shell has one sensible destination: the landing page. A
// history-based "back" would have to guess whether the previous entry is ours
// (`document.referrer` is empty for `rel="noreferrer"` links too), so a plain
// link is simpler and always correct.
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
