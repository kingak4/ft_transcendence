import { cookies } from 'next/headers';
import Link from 'next/link';

import BrandLink from './components/BrandLink';

import { clearSession } from './lib/logout';

const tags = ['Time tracking', 'Task planning', 'Progress tracking', 'Stats', 'Community'];

export default async function LandingPage() {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  return (
    <div className="relative flex min-h-screen bg-gradient-start-page">
      <BrandLink className="absolute left-6 top-6 text-brand-main-color" />

      <div className="flex flex-1 items-center justify-between gap-16 px-20 py-24">
        {/* Hero — left */}
        <div className="max-w-md">
          <h1 className="mb-4 text-4xl font-bold leading-tight text-brand-additional-color">
            Every <span className="text-brand-secondary-color">skill</span> has a story –<br />
            start yours!
          </h1>
          <p className="mb-10 text-sm leading-relaxed text-brand-main-color">
            Turn your daily grind into a journey of mastery.<br />
            <span className="text-brand-secondary-color">42Hub</span>{' '}
            is a tool designed for high-achievers who want to
            bridge the gap between &quot;getting things done&quot; and &quot;getting better&quot;.
          </p>
          <div className="flex flex-wrap justify-center gap-2">
            {tags.map((tag) => (
              <span
                key={tag}
                className="rounded-full bg-brand-additional-color px-4 py-1.5 text-xs font-bold text-brand-additional-color-2"
              >
                {tag}
              </span>
            ))}
          </div>
        </div>

        {/* Card — right */}
        {userId ? (
          <div className="w-72 rounded-2xl bg-brand-reversed-main-color p-8">
            <h2 className="mb-1 text-2xl font-bold text-brand-main-color">Welcome back!</h2>
            <p className="mb-8 text-sm text-brand-main-color">Continue your journey.</p>
            <Link
              href={`/${userId}`}
              className="mb-4 block rounded-lg bg-brand-secondary-color py-3 text-center text-sm font-bold text-brand-additional-color-2 transition-colors hover:brightness-125"
            >
              Go to my profile
            </Link>
            <form action={clearSession}>
              <button
                type="submit"
                className="w-full rounded-lg border border-brand-secondary-color py-3 text-sm font-bold text-brand-secondary-color transition-colors hover:bg-brand-secondary-color hover:text-brand-additional-color-2"
              >
                Log out
              </button>
            </form>
          </div>
        ) : (
          <div className="w-72 rounded-2xl bg-brand-reversed-main-color p-8">
            <h2 className="mb-1 text-2xl font-bold text-brand-main-color">Hello!</h2>
            <p className="mb-8 text-sm text-brand-main-color">Do your thing. Grind.</p>
            <Link
              href="/login"
              className="mb-4 block rounded-lg bg-brand-secondary-color py-3 text-center text-sm font-bold text-brand-additional-color-2 transition-colors hover:brightness-125"
            >
              Login
            </Link>
            <Link
              href="/home"
              className="mb-8 block rounded-lg border border-brand-secondary-color py-3 text-center text-sm font-bold text-brand-secondary-color transition-colors hover:bg-brand-secondary-color hover:text-brand-additional-color-2"
            >
              Continue as guest
            </Link>
            <p className="text-center text-xs text-brand-main-color">
              Don&apos;t have an account yet?{' '}
              <Link href="/register" className="font-bold text-brand-additional-color transition-colors hover:text-brand-secondary-color">
                Register
              </Link>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
