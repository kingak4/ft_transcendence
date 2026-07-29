'use client';

import Link from 'next/link';
import { useState, type ReactNode } from 'react';

import UserList from '../../components/UserList';
import UserSearch from '../../components/UserSearch';
import AddFriendButton from './AddFriendButton';
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
  /** Zero-based, matching the backend's `number` field. */
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
      <h2 className="text-on-surface mb-3 text-xl font-bold">Friends</h2>
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
        renderAction={(user) => (
          <RemoveFriendButton
            friendId={user.id}
            onRemoved={() =>
              setRemovedIds((prev) => new Set(prev).add(user.id))
            }
          />
        )}
      />
      {totalPages > 1 && (
        <nav
          aria-label="Friends pages"
          className="mt-3 flex items-center justify-between"
        >
          <PagerLink
            href={`/${currentUserId}?page=${page - 1}`}
            disabled={page === 0}
          >
            ‹ Prev
          </PagerLink>
          <span className="text-on-surface/60 text-xs">
            Page {page + 1} of {totalPages}
          </span>
          <PagerLink
            href={`/${currentUserId}?page=${page + 1}`}
            disabled={page >= totalPages - 1}
          >
            Next ›
          </PagerLink>
        </nav>
      )}
    </section>
  );
}

// A "disabled" <Link> is still a working anchor, so the ends of the range
// render as an inert <span> instead: same box, no navigation, and announced
// as disabled rather than as a link that does nothing.
function PagerLink({
  href,
  disabled,
  children,
}: {
  href: string;
  disabled: boolean;
  children: ReactNode;
}) {
  const base = 'rounded-lg px-3 py-1.5 text-xs font-medium transition-colors';

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
