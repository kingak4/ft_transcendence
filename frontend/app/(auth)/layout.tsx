import { cookies } from 'next/headers';
import Link from 'next/link';

import BrandLink from '../components/BrandLink';
import { logout } from '../lib/logout';

export default async function AuthLayout({ children }: { children: React.ReactNode }) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value;

  if (userId) {
    return (
      <div className="relative flex min-h-screen items-center justify-center bg-brand-main-color">
        <BrandLink className="absolute left-6 top-6 text-brand-reversed-main-color" />
        <div className="w-80 rounded-2xl bg-brand-reversed-main-color p-8">
          <h1 className="mb-1 text-2xl font-bold text-brand-main-color">Already logged in</h1>
          <p className="mb-6 text-sm text-brand-main-color/60">
            You are currently logged in to your account.
          </p>
          <Link
            href={`/${userId}`}
            className="mb-3 block rounded-lg bg-brand-secondary-color py-3 text-center text-sm font-bold text-brand-additional-color-2 transition-colors hover:brightness-125"
          >
            Go to my profile
          </Link>
          <form action={logout}>
            <button
              type="submit"
              className="w-full rounded-lg bg-white/10 py-3 text-sm font-medium text-brand-main-color/70 transition-colors hover:bg-white/20"
            >
              Log out
            </button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="relative min-h-screen">
      <BrandLink className="absolute left-6 top-6 text-brand-main-color" />
      {children}
    </div>
  );
}
