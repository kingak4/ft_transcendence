'use client';

import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { useState, type ReactNode } from 'react';

import UserList from '../../components/UserList';
import UserSearch from '../../components/UserSearch';
import AddFriendButton from './AddFriendButton';
import OpenChatLink from './OpenChatLink';
import RemoveFriendButton from './RemoveFriendButton';
import { searchUsersAction } from './actions';

export interface FriendCard {
  id: string;
  displayName: string;
  avatarId?: string;
}

interface Props {
  friends: FriendCard[];
  currentUserId: string;
  page: number;
  totalPages: number;
}

export default function FriendsPanel({
  friends,
  currentUserId,
  page,
  totalPages,
}: Props) {
  const t = useTranslations('friends');
  const [removedIds, setRemovedIds] = useState<Set<string>>(new Set());

  const friendCards = friends.filter((friend) => !removedIds.has(friend.id));
  const friendIds = new Set(friendCards.map((friend) => friend.id));

  return (
    <section>
      <UserSearch
        currentUserId={currentUserId}
        excludedIds={friendIds}
        searchAction={searchUsersAction}
        renderAction={(user, dismiss) => (
          <AddFriendButton friendId={user.id} onAdded={dismiss} />
        )}
        label={t('searchUsers')}
      />
      <UserList
        users={friendCards}
        emptyMessage={t('noFriends')}
        getHref={(user) => `/${user.id}`}
        renderAction={(user) => (
          <div className="flex items-center gap-3">
            <OpenChatLink friendId={user.id} />
            <RemoveFriendButton
              friendId={user.id}
              onRemoved={() =>
                setRemovedIds((prev) => new Set(prev).add(user.id))
              }
            />
          </div>
        )}
      />
      {totalPages > 1 && (
        <nav
          aria-label={t('pagesLabel')}
          className="mt-3 flex items-center justify-between"
        >
          <PagerLink href={`/friends?page=${page - 1}`} disabled={page === 0}>
            {t('prev')}
          </PagerLink>
          <span className="text-muted text-xs">
            {t('pageOf', { page: page + 1, total: totalPages })}
          </span>
          <PagerLink
            href={`/friends?page=${page + 1}`}
            disabled={page >= totalPages - 1}
          >
            {t('next')}
          </PagerLink>
        </nav>
      )}
    </section>
  );
}

function PagerLink({
  href,
  disabled,
  children,
}: {
  href: string;
  disabled: boolean;
  children: ReactNode;
}) {
  const base =
    'rounded-[10px] px-3 py-1.5 text-xs font-semibold transition-colors';

  if (disabled) {
    return (
      <span
        aria-disabled="true"
        className={`${base} text-on-elevated-surface/30`}
      >
        {children}
      </span>
    );
  }

  return (
    <Link
      href={href}
      scroll={false}
      className={`${base} text-on-elevated-surface bg-on-elevated-surface/10 hover:bg-on-elevated-surface/20`}
    >
      {children}
    </Link>
  );
}
