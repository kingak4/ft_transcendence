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

export default function UserList({ users, emptyMessage, renderAction }: Props) {
  if (users.length === 0) {
    return <p className="text-on-surface/40 text-sm">{emptyMessage}</p>;
  }

  return (
    <ul className="flex flex-col gap-2">
      {users.map((user) => {
        const avatarSrc = user.avatarId
          ? `/api/users/avatar/${user.avatarId}`
          : null;

        return (
          <li
            key={user.id}
            className="bg-inverse-surface text-on-inverse-surface flex items-center justify-between gap-3 rounded-xl p-3"
          >
            <div className="flex items-center gap-3">
              <Avatar
                src={avatarSrc}
                alt={`${user.displayName}'s avatar`}
                size={40}
              />
              <p className="font-medium">{user.displayName}</p>
            </div>
            {renderAction(user)}
          </li>
        );
      })}
    </ul>
  );
}
