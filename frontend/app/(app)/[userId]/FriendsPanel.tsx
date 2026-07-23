import SearchFriends from './SearchFriends';
import UserList from './UserList';
import RemoveFriendButton from './RemoveFriendButton';

interface Friend {
  displayName?: string;
  avatarId?: { val?: string };
}

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
  const friendCards = Object.entries(friends).map(([rawFriendId, friend]) => ({
    id: extractFriendId(rawFriendId),
    displayName: friend.displayName ?? 'Unknown User',
    avatarId: friend.avatarId?.val,
  }));
  const friendIds = new Set(friendCards.map((friend) => friend.id));

  return (
    <section>
      <h2 className="text-brand-reversed-main-color mb-3 text-xl font-bold">
        Friends
      </h2>
      <SearchFriends currentUserId={currentUserId} existingFriendIds={friendIds} />
      <UserList
        users={friendCards}
        emptyMessage="No friends yet."
        renderAction={(user) => <RemoveFriendButton friendId={user.id} />}
      />
    </section>
  );
}
