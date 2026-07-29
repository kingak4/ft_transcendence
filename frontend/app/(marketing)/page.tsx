import { cookies } from 'next/headers';

import AccentLink from '../components/AccentLink';
import Button from '../components/Button';
import Card from '../components/Card';
import Hero from '../components/Hero';
import SessionCard from '../components/SessionCard';

import { clearSession } from '../lib/logout';

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="flex flex-1 flex-wrap items-center justify-center gap-16">
      <Hero />

      {/* Card — right */}
      {userId ? (
        <SessionCard
          title="Welcome back!"
          subtitle="Continue your journey."
          userId={userId}
          logoutAction={clearSession}
        />
      ) : (
        <Card>
          <h2 className="mb-1 text-2xl font-bold">Hello!</h2>
          <p className="mb-8 text-sm">Do your thing. Grind.</p>
          <Button href="/login" className="mb-4">
            Login
          </Button>
          <p className="text-center text-xs">
            Don&apos;t have an account yet?{' '}
            <AccentLink href="/register">Register</AccentLink>
          </p>
        </Card>
      )}
    </div>
  );
}
