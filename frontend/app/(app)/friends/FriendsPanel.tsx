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
  /**
   * The signed-in user. Since unit 2a this is used for one thing only -
   * excluding yourself from search results. It used to build the pager URLs
   * too, back when this panel lived on `/[userId]`; the pager now targets
   * `/friends`, which needs no id at all.
   */
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
      {/* The "Friends" heading moved up to the route at position 26. It was an
          h2 with no h1 above it, and once the page named itself the two would
          have read as the same word twice. */}
      <UserSearch
        currentUserId={currentUserId}
        excludedIds={friendIds}
        searchAction={searchUsersAction}
        renderAction={(user, dismiss) => (
          <AddFriendButton friendId={user.id} onAdded={dismiss} />
        )}
      />
      {/* Two actions per row, where the export has one. Its single "Open chat"
          keeps its label and position but becomes the accent action; Remove
          takes over the pale treatment the export gave it. 12px between them is
          the dictionary's in-group gap - 6px would crowd two buttons whose own
          horizontal padding is 18px. */}
      <UserList
        users={friendCards}
        emptyMessage="No friends yet."
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
  // Radius and weight from the dictionary's small-control rows; the pager stays
  // at text-xs rather than adopting the row buttons' 14px, because it labels the
  // list rather than acting on it.
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
