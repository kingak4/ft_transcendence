import Image from 'next/image';

interface Friend {
  displayName?: string;
  avatarId?: { val?: string };
}

interface Props {
  friends: Record<string, Friend>;
}

export default function FriendsPanel({ friends }: Props) {
  const entries = Object.entries(friends);

  return (
    <section>
      <h2 className="text-brand-reversed-main-color mb-3 text-xl font-bold">
        Friends
      </h2>
      {entries.length === 0 ? (
        <p className="text-brand-reversed-main-color/40 text-sm">
          No friends yet.
        </p>
      ) : (
        <ul className="flex flex-col gap-2">
          {entries.map(([friendId, friend]) => {
            const avatarSrc = friend.avatarId?.val
              ? `/api/users/avatar/${friend.avatarId.val}`
              : null;

            return (
              <li
                key={friendId}
                className="bg-brand-reversed-main-color flex items-center gap-3 rounded-xl p-3"
              >
                {avatarSrc ? (
                  <Image
                    src={avatarSrc}
                    alt={`${friend.displayName ?? 'Friend'}'s avatar`}
                    width={40}
                    height={40}
                    className="h-10 w-10 rounded-full object-cover"
                  />
                ) : (
                  <div className="h-10 w-10 rounded-full bg-blue-200" />
                )}
                <p className="text-brand-main-color font-medium">
                  {friend.displayName ?? 'Unknown User'}
                </p>
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}
