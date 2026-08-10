'use client';

import Link from 'next/link';

import Avatar from '../../components/Avatar';
import ChatInterface from './ChatInterface';
import type { ChatUser } from './types';
import { usePresence } from '../../hooks/usePresence';
import { CHAT_DICT } from './dictionary';
import AddFriendButton from '../friends/AddFriendButton';

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
        {/* Unit 2b. Below `lg:` this conversation is the whole screen, so the
            list it came from needs a way back - `/chat` with no parameter is
            exactly that state, which is why a plain link does the job and no
            history handling is needed. Hidden from `lg:` up, where the list is
            still on screen and going "back" to it would mean nothing. */}
        <Link
          href="/chat"
          aria-label="Back to conversations"
          className="text-hub-muted hover:bg-hub-row-active hover:text-hub-on-surface -ml-2 rounded-lg p-2 transition-colors lg:hidden"
        >
          <svg width="20" height="20" viewBox="0 0 20 20" aria-hidden="true">
            <path
              d="M12.5 4 6.5 10l6 6"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </Link>
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
          <span
            className={`${isOnline ? 'text-hub-status' : 'text-gray-400'} text-xs font-semibold`}
          >
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
