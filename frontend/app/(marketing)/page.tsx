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
    <div className="flex flex-1 flex-col items-center justify-center gap-12 px-6 pb-10 pt-6">
      <Hero />

      {userId ? (
        <SessionCard
          title="Welcome back!"
          subtitle="Continue your journey."
          userId={userId}
          logoutAction={clearSession}
        />
      ) : (
        <Card>
          <h2 className="mb-1 text-xl font-extrabold">Hello!</h2>
          <p className="mb-6 text-sm font-medium">
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
