import Link from 'next/link';

import PresenceAvatar from '../../components/PresenceAvatar';
import type { Friend } from './types';

interface Props {
  friend: Friend;
  isActive: boolean;
  onClick?: () => void;
}

/**
 * A link rather than a button: selection is URL state (`?friend=`), which keeps
 * the page a Server Component with no client JS, and hands us keyboard access,
 * history and shareable URLs from the browser instead of from code.
 *
 * TODO(stomp): A real row carries more than a name - last-message preview,
 * its timestamp, and an unread badge driven by the message feed.
 * Await the backend `/chats` PR to provide this data, then pass it down as optional props.
 */
export default function FriendRow({ friend, isActive, onClick }: Props) {
  const avatarSrc = friend.avatarId ? `/api/users/avatar/${friend.avatarId}` : null;

  return (
    <Link
      href={`/chat?friend=${friend.id}`}
      onClick={onClick}
      // `page` is the right token here: this link points at the current view.
      aria-current={isActive ? 'page' : undefined}
      className={`flex items-center gap-3 rounded-xl px-3 py-2.5 transition-colors ${
        isActive ? 'bg-hub-row-active' : 'hover:bg-hub-row-active/60'
      }`}
    >
      <PresenceAvatar
        src={avatarSrc}
        alt={friend.name}
        size={40}
        initial={friend.initial}
        color={friend.color}
        online={friend.online}
      />
      {/* min-w-0 is required for `truncate`: a flex item defaults to
          min-width:auto and will not shrink below its text otherwise. */}
      <span className="text-hub-on-surface min-w-0 flex-1 truncate text-sm font-semibold">
        {friend.name}
      </span>
    </Link>
  );
}
