import { notFound, redirect } from 'next/navigation';

import { client } from '../../lib/api-clients';
import { logout } from '../../lib/logout';

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

  return (
    <div className="flex gap-6">
      {/* Main column */}
      <div className="flex min-w-0 flex-1 flex-col gap-6">
        {/* Profile banner */}
        <div className="flex items-start justify-between rounded-2xl bg-brand-secondary-color p-6">
          <div>
            <h1 className="mb-4 text-3xl font-bold text-brand-reversed-main-color">
              {displayName}
            </h1>
            <form action={logout}>
              <button
                type="submit"
                className="rounded-lg bg-brand-reversed-main-color px-4 py-2 text-sm font-bold text-brand-main-color transition-colors hover:brightness-90"
              >
                Log Out
              </button>
            </form>
          </div>

          <div className="relative shrink-0">
            {data.avatarId ? (
              <img
                src={`/api/users/avatar/${data.avatarId}`}
                alt={`${displayName}'s avatar`}
                className="h-24 w-24 rounded-full object-cover"
              />
            ) : (
              <div className="h-24 w-24 rounded-full bg-blue-200" />
            )}
            <button className="absolute bottom-0 right-0 rounded-full bg-white p-1.5 text-xs shadow transition-colors hover:bg-brand-main-color">
              Edit
            </button>
          </div>
        </div>

        {/* Statistics */}
        <section>
          <h2 className="mb-3 text-xl font-bold text-brand-reversed-main-color">Statistics</h2>
          <div className="flex gap-4">
            {stats.map((stat) => (
              <div key={stat.label} className="flex-1 rounded-xl bg-brand-reversed-main-color p-5">
                <p className="text-2xl font-bold text-brand-main-color">{stat.value}</p>
                <p className="text-sm text-brand-main-color/60">{stat.label}</p>
              </div>
            ))}
          </div>
        </section>

        {/* Work graph */}
        <section>
          <h2 className="mb-3 text-xl font-bold text-brand-reversed-main-color">Work graph</h2>
          <div className="flex h-56 items-center justify-center rounded-xl bg-brand-reversed-main-color">
            <p className="text-sm text-brand-main-color/40">
              Chart placeholder — install recharts to render this
            </p>
          </div>
        </section>
      </div>

      {/* Friends panel */}
      <aside className="w-60 shrink-0 self-start rounded-2xl bg-white p-4 shadow-sm">
        <h2 className="mb-3 text-base font-bold text-brand-reversed-main-color">Friends</h2>
        <input
          type="text"
          placeholder="Enter name..."
          className="mb-4 w-full rounded-lg bg-brand-main-color px-3 py-2 text-sm text-brand-reversed-main-color outline-none placeholder:text-brand-reversed-main-color/40 focus:ring-1 focus:ring-brand-secondary-color"
        />
        <p className="text-sm text-brand-reversed-main-color/40">Friends list coming soon</p>
      </aside>
    </div>
  );
}
