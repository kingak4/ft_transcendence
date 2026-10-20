'use client';

import Link from 'next/link';
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
      />
      <UserList
        users={friendCards}
        emptyMessage="No friends yet."
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
          aria-label="Friends pages"
          className="mt-3 flex items-center justify-between"
        >
          <PagerLink href={`/friends?page=${page - 1}`} disabled={page === 0}>
            ‹ Prev
          </PagerLink>
          <span className="text-on-surface/60 text-xs">
            Page {page + 1} of {totalPages}
          </span>
          <PagerLink
            href={`/friends?page=${page + 1}`}
            disabled={page >= totalPages - 1}
          >
            Next ›
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
      className={`${base} text-on-elevated-surface/60 bg-on-elevated-surface/10 hover:bg-on-elevated-surface/20`}
    >
      {children}
    </Link>
  );
}
