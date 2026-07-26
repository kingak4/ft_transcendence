import { cookies } from 'next/headers';

import BareLayout from '../components/BareLayout';
import Button from '../components/Button';
import Card from '../components/Card';
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
      <BareLayout>
        <div className="flex flex-1 items-center justify-center">
          <Card>
            <h1 className="mb-1 text-2xl font-bold">Already logged in</h1>
            <p className="text-on-inverse-surface/60 mb-6 text-sm">
              You are currently logged in to your account.
            </p>
            <div className="mb-3">
              <Button href={`/${userId}`}>Go to my profile</Button>
            </div>
            <form action={logout}>
              <Button type="submit" variant="outline">
                Log out
              </Button>
            </form>
          </Card>
        </div>
      </BareLayout>
    );
  }

  return <BareLayout>{children}</BareLayout>;
}
