import Link from 'next/link';
import { useTranslations } from 'next-intl';
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
  getHref?: (user: UserListItem) => string;
}

export default function UserList({
  users,
  emptyMessage,
  renderAction,
  getHref,
}: Props) {
  const t = useTranslations('common');

  if (users.length === 0) {
    return (
      <p className="text-on-surface/40 text-sm font-medium">{emptyMessage}</p>
    );
  }

  return (
    <ul className="flex flex-col gap-3">
      {users.map((user) => {
        const avatarSrc = user.avatarId
          ? `/api/users/avatar/${user.avatarId}`
          : null;

        const identity = (
          <>
            <Avatar
              src={avatarSrc}
              alt={getHref ? '' : t('avatarAlt', { name: user.displayName })}
              size={44}
            />
            <p className="text-sm font-bold group-hover:underline">
              <bdi>{user.displayName}</bdi>
            </p>
          </>
        );

        return (
          <li
            key={user.id}
            className="bg-elevated-surface text-on-elevated-surface px-4.5 flex items-center justify-between gap-4 rounded-2xl py-3.5 shadow-[0_6px_18px_var(--color-shadow-row)]"
          >
            {getHref ? (
              <Link
                href={getHref(user)}
                className="group flex items-center gap-4"
              >
                {identity}
              </Link>
            ) : (
              <div className="flex items-center gap-4">{identity}</div>
            )}
            {renderAction(user)}
          </li>
        );
      })}
    </ul>
  );
}
