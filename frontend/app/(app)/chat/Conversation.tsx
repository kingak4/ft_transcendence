'use client';

import Link from 'next/link';
import { useTranslations } from 'next-intl';

import Avatar from '../../components/Avatar';
import Icon from '../../components/Icon';
import ChatInterface from './ChatInterface';
import type { ChatUser } from './types';
import { usePresence } from '../../hooks/usePresence';
import AddFriendButton from '../friends/AddFriendButton';

interface Props {
  user: ChatUser;
  initialChatId: string | null;
  myUserId: string | null;
  isFriend?: boolean;
}

export default function Conversation({ user, initialChatId, myUserId, isFriend }: Props) {
  const t = useTranslations('chat.conversation');
  const tCommon = useTranslations('common');
  const avatarSrc = user.avatarId ? `/api/users/avatar/${user.avatarId}` : null;
  const { onlineStatus } = usePresence([user.id]);
  const isOnline = onlineStatus[user.id] ?? user.online;
  const statusText = isOnline ? tCommon('online') : tCommon('offline');

  return (
    <section className="bg-sunken-surface flex min-h-0 flex-1 flex-col">
      <header className="bg-elevated-surface border-elevated-border flex shrink-0 items-center gap-3 border-b px-5 py-4">
        <Link
          href="/chat"
          aria-label={t('back')}
          className="text-muted hover:bg-row-active hover:text-on-elevated-surface -ms-2 rounded-lg p-2 transition-colors lg:hidden"
        >
          <Icon name="chevron-left" size="md" mirrorInRtl />
        </Link>
        <Avatar
          src={avatarSrc}
          alt=""
          size={40}
          initial={user.initial}
          color={user.color}
        />
        <div className="flex flex-col">
          <h2 className="text-on-elevated-surface text-sm font-bold">
            <bdi>{user.name}</bdi>
          </h2>
          <span
            className={`${isOnline ? 'text-online-text' : 'text-offline-text'} text-xs font-semibold`}
          >
            {statusText}
          </span>
        </div>
      </header>

      {isFriend === false && (
        <div className="bg-elevated-surface border-elevated-border flex shrink-0 items-center justify-between border-b px-5 py-3">
          <span className="text-muted text-sm font-medium">{t('notFriendBanner')}</span>
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
