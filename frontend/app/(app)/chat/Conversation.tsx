'use client';

import Avatar from '../../components/Avatar';
import ChatInterface from './ChatInterface';
import type { ChatUser } from './types';
import { usePresence } from '../../hooks/usePresence';
import { CHAT_DICT } from './dictionary';
import AddFriendButton from '../[userId]/AddFriendButton';

interface Props {
  user: ChatUser;
  initialChatId: string | null;
  myUserId: string | null;
  isFriend?: boolean;
}

export default function Conversation({ user, initialChatId, myUserId, isFriend }: Props) {
  const avatarSrc = user.avatarId ? `/api/users/avatar/${user.avatarId}` : null;
  const { onlineStatus } = usePresence([user.id]);
  const isOnline = onlineStatus[user.id] ?? user.online;
  const statusText = isOnline ? CHAT_DICT.conversation.online : CHAT_DICT.conversation.offline;

  return (
    <section className="bg-hub-panel-sunken flex min-h-0 flex-1 flex-col">
      <header className="bg-hub-panel border-hub-border flex shrink-0 items-center gap-3 border-b px-5 py-4">
        <Avatar
          src={avatarSrc}
          alt={user.name}
          size={40}
          initial={user.initial}
          color={user.color}
        />
        <div className="flex flex-col">
          <h2 className="text-hub-on-surface text-sm font-bold">
            {user.name}
          </h2>
          <span className={`${isOnline ? 'text-hub-status' : 'text-gray-400'} text-xs font-semibold`}>
            {statusText}
          </span>
        </div>
      </header>

      {isFriend === false && (
        <div className="bg-hub-panel border-hub-border flex shrink-0 items-center justify-between border-b px-5 py-3">
          <span className="text-hub-muted text-sm font-medium">{CHAT_DICT.conversation.notFriendBanner}</span>
          <AddFriendButton friendId={user.id} onAdded={() => {}} />
        </div>
      )}

      {/* 
       * TODO(stomp): History is finite. Loading older messages on upward scroll 
       * means preserving scroll position while prepending, or the view jumps.
       * Needs an IntersectionObserver on the top of the message list.
       */}
      <ChatInterface user={user} initialChatId={initialChatId} myUserId={myUserId} />
    </section>
  );
}
