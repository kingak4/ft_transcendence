import { useLocale, useTranslations } from 'next-intl';

import Icon from '../../components/Icon';
import MessageStatus from './MessageStatus';
import type { ChatMessage } from './types';

interface Props {
  message: ChatMessage;
  isMine: boolean;
  senderName: string;
  onDelete?: () => void;
  /*
   * TODO(stomp): Only 'sent' is shown today - every message of mine that renders
   * was echoed back by the server. MessageStatus already draws 'sending' / 'failed',
   * but they need the backend to accept and echo a `clientMessageId` (correlation ID)
   * so the frontend can reconcile optimistic messages reliably.
   */
}

export default function MessageBubble({
  message,
  isMine,
  senderName,
  onDelete,
}: Props) {
  const t = useTranslations('chat.messages');
  // App language, browser time zone: next-intl's formatter would use the
  // server's zone, which is wrong for a timestamp rendered on the client.
  const locale = useLocale();

  const formatTime = (timeStr?: string) => {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffDays === 0 && now.getDate() === date.getDate()) {
      return date.toLocaleTimeString(locale, {
        hour: '2-digit',
        minute: '2-digit',
      });
    } else if (
      diffDays === 1 ||
      (diffDays === 0 && now.getDate() !== date.getDate())
    ) {
      return t('yesterday');
    } else {
      return date.toLocaleDateString(locale);
    }
  };

  return (
    <div
      className={`group flex max-w-[420px] flex-col ${isMine ? 'self-end' : 'self-start'}`}
    >
      <div
        className={`flex items-center gap-2 ${isMine ? 'flex-row-reverse' : 'flex-row'}`}
      >
        {/* The tail corner is logical (ee/es), so it follows the bubble's side in RTL. */}
        <div
          className={
            isMine
              ? `rounded-[18px] rounded-ee-[4px] px-4 py-2.5 text-sm leading-relaxed ${
                  message.isDeleted
                    ? 'bg-elevated-surface/30 text-muted italic'
                    : 'bg-bubble text-on-accent'
                }`
              : `rounded-[18px] rounded-es-[4px] px-4 py-2.5 text-sm leading-relaxed shadow-[0_3px_10px_var(--color-shadow-subtle)] ${
                  message.isDeleted
                    ? 'bg-elevated-surface/30 text-muted italic'
                    : 'bg-elevated-surface text-on-elevated-surface'
                }`
          }
        >
          <span className="sr-only">
            {t('senderPrefix', { name: senderName })}
          </span>
          <bdi>{message.isDeleted ? t('deleted') : message.content}</bdi>
        </div>
        {isMine && onDelete && !message.isDeleted && (
          <button
            onClick={onDelete}
            aria-label={t('delete')}
            className="text-muted hover:text-danger p-1 opacity-0 transition-opacity group-hover:opacity-100 focus-visible:opacity-100"
          >
            <Icon name="trash" size="sm" />
          </button>
        )}
      </div>
      <div
        className={`text-timestamp mt-1 flex items-center gap-1 ${isMine ? 'justify-end' : 'justify-start'}`}
      >
        <time dateTime={message.time} className="text-[11px] font-semibold">
          {formatTime(message.time)}
        </time>
        {isMine && !message.isDeleted && <MessageStatus status="sent" />}
      </div>
    </div>
  );
}
