import { cookies } from 'next/headers';
import Link from 'next/link';

import BrandLink from './components/BrandLink';

import { clearSession } from './lib/logout';

const tags = [
  'Time tracking',
  'Task planning',
  'Progress tracking',
  'Stats',
  'Community',
];

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="bg-gradient-start-page relative flex min-h-screen">
      <BrandLink className="text-brand-main-color absolute left-6 top-6" />

      <div className="flex flex-1 items-center justify-between gap-16 px-20 py-24">
        {/* Hero — left */}
        <div className="max-w-md">
          <h1 className="text-brand-additional-color mb-4 text-4xl font-bold leading-tight">
            Every <span className="text-brand-secondary-color">skill</span> has
            a story –<br />
            start yours!
          </h1>
          <p className="text-brand-main-color mb-10 text-sm leading-relaxed">
            Turn your daily grind into a journey of mastery.
            <br />
            <span className="text-brand-secondary-color">42Hub</span> is a tool
            designed for high-achievers who want to bridge the gap between
            &quot;getting things done&quot; and &quot;getting better&quot;.
          </p>
          <div className="flex flex-wrap justify-center gap-2">
            {tags.map((tag) => (
              <span
                key={tag}
                className="bg-brand-additional-color text-brand-additional-color-2 rounded-full px-4 py-1.5 text-xs font-bold"
              >
                {tag}
              </span>
            ))}
          </div>
        </div>

        {/* Card — right */}
        {userId ? (
          <div className="bg-brand-reversed-main-color w-72 rounded-2xl p-8">
            <h2 className="text-brand-main-color mb-1 text-2xl font-bold">
              Welcome back!
            </h2>
            <p className="text-brand-main-color mb-8 text-sm">
              Continue your journey.
            </p>
            <Link
              href={`/${userId}`}
              className="bg-brand-secondary-color text-brand-additional-color-2 mb-4 block rounded-lg py-3 text-center text-sm font-bold transition-colors hover:brightness-125"
            >
              Go to my profile
            </Link>
            <form action={clearSession}>
              <button
                type="submit"
                className="border-brand-secondary-color text-brand-secondary-color hover:bg-brand-secondary-color hover:text-brand-additional-color-2 w-full rounded-lg border py-3 text-sm font-bold transition-colors"
              >
                Log out
              </button>
            </form>
          </div>
        ) : (
          <div className="bg-brand-reversed-main-color w-72 rounded-2xl p-8">
            <h2 className="text-brand-main-color mb-1 text-2xl font-bold">
              Hello!
            </h2>
            <p className="text-brand-main-color mb-8 text-sm">
              Do your thing. Grind.
            </p>
            <Link
              href="/login"
              className="bg-brand-secondary-color text-brand-additional-color-2 mb-4 block rounded-lg py-3 text-center text-sm font-bold transition-colors hover:brightness-125"
            >
              Login
            </Link>
            <Link
              href="/home"
              className="border-brand-secondary-color text-brand-secondary-color hover:bg-brand-secondary-color hover:text-brand-additional-color-2 mb-8 block rounded-lg border py-3 text-center text-sm font-bold transition-colors"
            >
              Continue as guest
            </Link>
            <p className="text-brand-main-color text-center text-xs">
              Don&apos;t have an account yet?{' '}
              <Link
                href="/register"
                className="text-brand-additional-color hover:text-brand-secondary-color font-bold transition-colors"
              >
                Register
              </Link>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
