'use client';

import { useState } from 'react';

import UserList from '../../components/UserList';
import UserSearch from '../../components/UserSearch';
import type { components } from '../../types/api';
import AddFriendButton from './AddFriendButton';
import RemoveFriendButton from './RemoveFriendButton';
import { searchUsersAction } from './actions';

type Friend = components['schemas']['UserDetails'];

interface Props {
  friends: Record<string, Friend>;
  currentUserId: string;
}

// Workaround for a backend serialization quirk: Jackson serializes the
// `Map<FriendId, UserDetails>` key using FriendId's default toString()
// (e.g. "FriendId[val=<uuid>]") instead of the bare UUID. Remove this once
// the backend serializes the map key as a plain string.
// TODO: #59
function extractFriendId(rawKey: string): string {
  return rawKey.match(/val=([^\]]+)/)?.[1] ?? rawKey;
}

export default function FriendsPanel({ friends, currentUserId }: Props) {
  const [removedIds, setRemovedIds] = useState<Set<string>>(new Set());

  const friendCards = Object.entries(friends)
    .map(([rawFriendId, friend]) => ({
      id: extractFriendId(rawFriendId),
      displayName: friend.displayName ?? 'Unknown User',
      avatarId: friend.avatarId?.val,
    }))
    .filter((friend) => !removedIds.has(friend.id));
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
    </section>
  );
}
