const user = {
  name: 'Janusz Pietruszka',
  joinedDate: 'May 2026',
  age: 26,
  friendsCount: 4,
};

const stats = [
  { value: 3, label: 'Day streak' },
  { value: 1273, label: 'Minutes tracked' },
];

const friends = [
  { id: 1, name: 'Johnny', status: 'Active', statusColor: 'bg-green-400' },
  { id: 2, name: 'Adelada', status: 'Away', statusColor: 'bg-amber-400' },
  { id: 3, name: 'Vic', status: 'Active', statusColor: 'bg-green-400' },
  { id: 4, name: 'Ben', status: 'Inactive', statusColor: 'bg-red-400' },
];

export default function UserProfilePage() {
  return (
    <div className="flex gap-6">
      {/* Main column */}
      <div className="flex min-w-0 flex-1 flex-col gap-6">

        {/* Profile banner */}
        <div className="flex items-start justify-between rounded-2xl bg-brand-secondary-color p-6">
          <div>
            <h1 className="mb-4 text-3xl font-bold text-brand-reversed-main-color">
              {user.name}
            </h1>
            <div className="flex flex-col gap-2 text-sm text-brand-reversed-main-color/80">
              <span>Joined {user.joinedDate}</span>
              <span>Age {user.age}</span>
              <span>Friends {user.friendsCount}</span>
            </div>
          </div>

          <div className="relative shrink-0">
            <div className="h-24 w-24 rounded-full bg-blue-200" />
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
        <div className="flex flex-col gap-4">
          {friends.map((friend) => (
            <div key={friend.id} className="flex items-center gap-3">
              <div className="h-9 w-9 shrink-0 rounded-full bg-brand-main-color" />
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold text-brand-reversed-main-color">
                  {friend.name}
                </p>
                <div className="flex items-center gap-1.5">
                  <span className={`h-2 w-2 rounded-full ${friend.statusColor}`} />
                  <p className="text-xs text-brand-reversed-main-color/50">{friend.status}</p>
                </div>
              </div>
              <button
                type="button"
                aria-label="Message friend"
                className="shrink-0 text-brand-reversed-main-color/30 transition-colors hover:text-brand-reversed-main-color"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </button>
            </div>
          ))}
        </div>
      </aside>
    </div>
  );
}
