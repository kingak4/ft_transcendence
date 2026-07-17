import { cookies } from 'next/headers';
import Link from 'next/link';

import BrandLink from '../components/BrandLink';
import { logout } from '../lib/logout';

export default async function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value;

  if (userId) {
    return (
      <div className="bg-brand-main-color relative flex min-h-screen items-center justify-center">
        <BrandLink className="text-brand-reversed-main-color absolute left-6 top-6" />
        <div className="bg-brand-reversed-main-color w-80 rounded-2xl p-8">
          <h1 className="text-brand-main-color mb-1 text-2xl font-bold">
            Already logged in
          </h1>
          <p className="text-brand-main-color/60 mb-6 text-sm">
            You are currently logged in to your account.
          </p>
          <Link
            href={`/${userId}`}
            className="bg-brand-secondary-color text-brand-additional-color-2 mb-3 block rounded-lg py-3 text-center text-sm font-bold transition-colors hover:brightness-125"
          >
            Go to my profile
          </Link>
          <form action={logout}>
            <button
              type="submit"
              className="text-brand-main-color/70 w-full rounded-lg bg-white/10 py-3 text-sm font-medium transition-colors hover:bg-white/20"
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
      <BrandLink className="text-brand-main-color absolute left-6 top-6" />
      {children}
    </div>
  );
}
