'use client';

import { useRouter } from 'next/navigation';

// "Is the previous history entry one of ours?" The browser never says so
// directly, so this combines two clues:
// - history.length <= 1: a fresh tab or pasted link - there is nothing to go
//   back to.
// - document.referrer is only set on a full page load. After a client-side
//   <Link> navigation it still names whatever loaded the app first - often
//   nothing at all - so an empty referrer with history behind it means "we got
//   here inside the app". A referrer from another origin means the previous
//   entry is someone else's site, and back() would leave ft_transcendence.
function previousEntryIsInApp(): boolean {
  if (window.history.length <= 1) return false;
  if (!document.referrer) return true;
  return new URL(document.referrer).origin === window.location.origin;
}

export default function BackButton() {
  const router = useRouter();

  const goBack = () => {
    if (previousEntryIsInApp()) {
      router.back();
    } else {
      router.push('/');
    }
  };

  return (
    <button
      type="button"
      onClick={goBack}
      className="text-on-surface self-start text-sm font-semibold hover:underline"
    >
      ← Back
    </button>
  );
}
