import { notFound, redirect } from 'next/navigation';

import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';
import EditAvatarButton from './EditAvatarButton';
import FirstLoginSetup from './FirstLoginSetup';

interface Props {
  params: Promise<{ userId: string }>;
}

const stats = [
  { value: '—', label: 'Day streak' },
  { value: '—', label: 'Minutes tracked' },
];

export default async function UserProfilePage({ params }: Props) {
  const { userId } = await params;

  const { data, response } = await client.GET('/users/{userId}/details', {
    params: { path: { userId } },
  });

  if (response.status === 401 || response.status === 403) {
    redirect('/login');
  }

  if (response.status === 404 || !data) {
    notFound();
  }

  const displayName = data.displayName ?? 'Unknown User';

  const isFirstLogin =
    data.displayName === 'User' &&
    data.avatarId === '00000000-0000-0000-0000-000000000000';

  return (
    <>
      {isFirstLogin && <FirstLoginSetup />}
      <div className="flex gap-6">
        {/* Main column */}
        <div className="flex min-w-0 flex-1 flex-col gap-6">
          {/* Profile banner */}
          <div className="bg-brand-secondary-color flex items-start justify-between rounded-2xl p-6">
            <div>
              <h1 className="text-brand-reversed-main-color mb-4 text-3xl font-bold">
                {displayName}
              </h1>
              <form action={logout}>
                <button
                  type="submit"
                  className="bg-brand-reversed-main-color text-brand-main-color rounded-lg px-4 py-2 text-sm font-bold transition-colors hover:brightness-90"
                >
                  Log Out
                </button>
              </form>
            </div>

            <EditAvatarButton
              avatarId={data.avatarId}
              displayName={displayName}
            />
          </div>

          {/* Statistics */}
          <section>
            <h2 className="text-brand-reversed-main-color mb-3 text-xl font-bold">
              Statistics
            </h2>
            <div className="flex gap-4">
              {stats.map((stat) => (
                <div
                  key={stat.label}
                  className="bg-brand-reversed-main-color flex-1 rounded-xl p-5"
                >
                  <p className="text-brand-main-color text-2xl font-bold">
                    {stat.value}
                  </p>
                  <p className="text-brand-main-color/60 text-sm">
                    {stat.label}
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* Work graph */}
          <section>
            <h2 className="text-brand-reversed-main-color mb-3 text-xl font-bold">
              Work graph
            </h2>
            <div className="bg-brand-reversed-main-color flex h-56 items-center justify-center rounded-xl">
              <p className="text-brand-main-color/40 text-sm">
                Chart placeholder — install recharts to render this
              </p>
            </div>
          </section>
        </div>

        {/* Friends panel */}
        <aside className="w-60 shrink-0 self-start rounded-2xl bg-white p-4 shadow-sm">
          <h2 className="text-brand-reversed-main-color mb-3 text-base font-bold">
            Friends
          </h2>
          <input
            type="text"
            placeholder="Enter name..."
            className="bg-brand-main-color text-brand-reversed-main-color placeholder:text-brand-reversed-main-color/40 focus:ring-brand-secondary-color mb-4 w-full rounded-lg px-3 py-2 text-sm outline-none focus:ring-1"
          />
          <p className="text-brand-reversed-main-color/40 text-sm">
            Friends list coming soon
          </p>
        </aside>
      </div>
    </>
  );
}
