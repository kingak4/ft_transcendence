'use client';

import Avatar from '../../components/Avatar';
import ChatInterface from './ChatInterface';
import type { Friend } from './types';
import { usePresence } from '../../hooks/usePresence';

interface Props {
  friend: Friend;
  initialChatId: string | null;
  myUserId: string | null;
}

export default function Conversation({ friend, initialChatId, myUserId }: Props) {
  const avatarSrc = friend.avatarId ? `/api/users/avatar/${friend.avatarId}` : null;
  const { onlineStatus } = usePresence([friend.id]);
  const isOnline = onlineStatus[friend.id] ?? friend.online;
  const statusText = isOnline ? 'Online' : 'Offline';

  return (
    <section className="bg-hub-panel-sunken flex min-h-0 flex-1 flex-col">
      <header className="bg-hub-panel border-hub-border flex shrink-0 items-center gap-3 border-b px-5 py-4">
        <Avatar
          src={avatarSrc}
          alt={friend.name}
          size={40}
          initial={friend.initial}
          color={friend.color}
        />
        <div className="flex flex-col">
          <h2 className="text-hub-on-surface text-sm font-bold">
            {friend.name}
          </h2>
          <span className={`${isOnline ? 'text-hub-status' : 'text-gray-400'} text-xs font-semibold`}>
            {statusText}
          </span>
        </div>
      </header>

      <ChatInterface friend={friend} initialChatId={initialChatId} myUserId={myUserId} />
    </section>
  );
}
