import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import AccentLink from '../components/AccentLink';
import Button from '../components/Button';
import Card from '../components/Card';
import Hero from '../components/Hero';
import SessionCard from '../components/SessionCard';

import { client } from '../lib/api-clients';
import { clearSession } from '../lib/logout';

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  if (userId) {
    const { response } = await client.GET('/users/{userId}/details', {
      params: { path: { userId } },
    });

    if (response.status === 401 || response.status === 403 || response.status === 404) {
      redirect('/api/auth/logout');
    }
  }

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
          <Button href="/login" fullWidth>
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
