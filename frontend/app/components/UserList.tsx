import type { ReactNode } from 'react';

import Avatar from './Avatar';

export interface UserListItem {
  id: string;
  displayName: string;
  avatarId?: string;
}

interface Props {
  users: UserListItem[];
  emptyMessage: string;
  renderAction: (user: UserListItem) => ReactNode;
}

// The export's result row (isSearch, line 315) lifts itself with a shadow and
// carries no border at all - we did the opposite, a 1px border and no shadow.
// Swapping them is the substantive change here; the rest is the dictionary's
// "wiersz listy" rows: 16px radius, 14x18px padding, 16px between children.
//
// The 44px avatar closes a 12.5 entry that has been assigned to this position
// since the inventory was written. `size` is a number passed by the caller, so
// it could only ever be fixed at a call site - and this is the call site both
// friends and search results go through.
export default function UserList({ users, emptyMessage, renderAction }: Props) {
  if (users.length === 0) {
    return <p className="text-on-surface/40 text-sm font-medium">{emptyMessage}</p>;
  }

  return (
    <ul className="flex flex-col gap-3">
      {users.map((user) => {
        const avatarSrc = user.avatarId
          ? `/api/users/avatar/${user.avatarId}`
          : null;

        return (
          <li
            key={user.id}
            className="bg-elevated-surface text-on-elevated-surface flex items-center justify-between gap-4 rounded-2xl px-4.5 py-3.5 shadow-[0_6px_18px_rgba(10,42,77,0.07)]"
          >
            <div className="flex items-center gap-4">
              <Avatar
                src={avatarSrc}
                alt={`${user.displayName}'s avatar`}
                size={44}
              />
              <p className="text-sm font-bold">{user.displayName}</p>
            </div>
            {renderAction(user)}
          </li>
        );
      })}
    </ul>
  );
}
