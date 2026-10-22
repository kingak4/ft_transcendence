import Link from 'next/link';

import PresenceAvatar from '../../components/PresenceAvatar';
import type { ChatUser } from './types';

interface Props {
  user: ChatUser;
  isActive: boolean;
  onClick?: () => void;
}

/**
 * TODO(stomp): A real row carries more than a name - last-message preview,
 * its timestamp, and an unread badge driven by the message feed.
 * Await the backend `/chats` PR to provide this data, then pass it down as optional props.
 */
export default function UserRow({ user, isActive, onClick }: Props) {
  const avatarSrc = user.avatarId ? `/api/users/avatar/${user.avatarId}` : null;

  return (
    <Link
      href={`/chat?friend=${user.id}`}
      onClick={onClick}
      aria-current={isActive ? 'page' : undefined}
      className={`flex items-center gap-3 rounded-xl px-3 py-2.5 transition-colors ${
        isActive ? 'bg-row-active' : 'hover:bg-row-active/60'
      }`}
    >
      <PresenceAvatar
        src={avatarSrc}
        alt=""
        size={40}
        initial={user.initial}
        color={user.color}
        online={user.online}
      />
      <span className="text-on-elevated-surface min-w-0 flex-1 truncate text-sm font-semibold">
        {user.name}
      </span>
    </Link>
  );
}
