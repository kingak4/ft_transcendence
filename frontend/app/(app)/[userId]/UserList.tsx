import Image from 'next/image';
import type { ReactNode } from 'react';

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
    return (
      <p className="text-brand-reversed-main-color/40 text-sm">
        {emptyMessage}
      </p>
    );
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
            className="bg-brand-reversed-main-color flex items-center justify-between gap-3 rounded-xl p-3"
          >
            <div className="flex items-center gap-3">
              {avatarSrc ? (
                <Image
                  src={avatarSrc}
                  alt={`${user.displayName}'s avatar`}
                  width={40}
                  height={40}
                  className="h-10 w-10 rounded-full object-cover"
                />
              ) : (
                <div className="h-10 w-10 rounded-full bg-blue-200" />
              )}
              <p className="text-brand-main-color font-medium">
                {user.displayName}
              </p>
            </div>
            {renderAction(user)}
          </li>
        );
      })}
    </ul>
  );
}
