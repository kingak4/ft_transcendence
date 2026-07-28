import { cookies } from 'next/headers';

import BareLayout from '../components/BareLayout';
import SessionCard from '../components/SessionCard';
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
          <SessionCard
            headingLevel="h1"
            title="Already logged in"
            subtitle="You are currently logged in to your account."
            userId={userId}
            logoutAction={logout}
          />
        </div>
      </BareLayout>
    );
  }

  return <BareLayout>{children}</BareLayout>;
}
