import { cookies } from 'next/headers';
import Link from 'next/link';

import Button from '../components/Button';
import Card from '../components/Card';
import Hero from '../components/Hero';

import { clearSession } from '../lib/logout';

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="flex flex-1 flex-wrap items-center justify-center gap-16">
      <Hero />

      {/* Card — right */}
      {userId ? (
        <Card>
          <h2 className="mb-1 text-2xl font-bold">Welcome back!</h2>
          <p className="mb-8 text-sm">Continue your journey.</p>
          <div className="mb-4">
            <Button href={`/${userId}`}>Go to my profile</Button>
          </div>
          <form action={clearSession}>
            <Button type="submit" variant="outline">
              Log out
            </Button>
          </form>
        </Card>
      ) : (
        <Card>
          <h2 className="mb-1 text-2xl font-bold">Hello!</h2>
          <p className="mb-8 text-sm">Do your thing. Grind.</p>
          <div className="mb-4">
            <Button href="/login">Login</Button>
          </div>
          <div className="mb-8">
            <Button href="/home" variant="outline">
              Continue as guest
            </Button>
          </div>
          <p className="text-center text-xs">
            Don&apos;t have an account yet?{' '}
            <Link
              href="/register"
              className="text-primary font-bold transition-colors hover:brightness-125"
            >
              Register
            </Link>
          </p>
        </Card>
      )}
    </div>
  );
}
