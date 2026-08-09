import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import AccentLink from '../components/AccentLink';
import Button from '../components/Button';
import Card from '../components/Card';
import Hero from '../components/Hero';
import SessionCard from '../components/SessionCard';

import { clearSession } from '../lib/logout';
import { assertValidSession } from '../lib/session';

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  if (userId) {
    const isValidSession = await assertValidSession();

    if (!isValidSession) {
      redirect('/auth/logout');
    }
  }

  return (
    // Position 21: a centred vertical stack, following the export's landing.
    // It was a `flex-wrap` row with Hero and the card side by side, which had
    // the side effect of showing two different arrangements at the two review
    // widths - the row collapsed to a stack below roughly 800px on its own.
    // Now both widths show the same thing and only the spacing changes.
    //
    // 48px between the hero column and the card is the export's `gap:48px`;
    // 24/40px of page padding is its `padding:24px 24px 40px`.
    <div className="flex flex-1 flex-col items-center justify-center gap-12 px-6 pb-10 pt-6">
      <Hero />

      {/* Card — below the hero, as in the export */}
      {userId ? (
        <SessionCard
          title="Welcome back!"
          subtitle="Continue your journey."
          userId={userId}
          logoutAction={clearSession}
        />
      ) : (
        // This card and `SessionCard` are the same slot in two session states,
        // so they take the same dictionary rows: heading 20px/800, subtitle
        // 14px/500 muted. They were 24px/700 and unweighted, which made the
        // page change shape depending on whether you were signed in.
        <Card>
          <h2 className="mb-1 text-xl font-extrabold">Hello!</h2>
          <p className="text-on-elevated-surface/60 mb-6 text-sm font-medium">
            Do your thing. Grind.
          </p>
          <Button href="/login" fullWidth>
            Login
          </Button>
          <p className="mt-4 text-center text-xs">
            Don&apos;t have an account yet?{' '}
            <AccentLink href="/register">Register</AccentLink>
          </p>
        </Card>
      )}
    </div>
  );
}
