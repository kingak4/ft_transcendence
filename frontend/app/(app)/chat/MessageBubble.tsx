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
  const formatTime = (timeStr?: string) => {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffDays === 0 && now.getDate() === date.getDate()) {
      return date.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      });
    } else if (
      diffDays === 1 ||
      (diffDays === 0 && now.getDate() !== date.getDate())
    ) {
      return 'Yesterday';
    } else {
      return date.toLocaleDateString();
    }
  };

  return (
    <div
      className={`group flex max-w-[420px] flex-col ${isMine ? 'self-end' : 'self-start'}`}
    >
      <div
        className={`flex items-center gap-2 ${isMine ? 'flex-row-reverse' : 'flex-row'}`}
      >
        {/*
         * TODO(rtl #86): mirror these four corners. Re-tagged from
         * design-migration at Krok 8 - RTL was deferred, so the work is still
         * open, but it no longer belongs to the migration that closed.
         * Everything else here is already direction-agnostic (self-start/end,
         * text-start/end), so this is the only bit that needs an `rtl:` variant.
         */}
        <div
          className={
            isMine
              ? `rounded-[18px_18px_4px_18px] px-4 py-2.5 text-sm leading-relaxed ${
                  message.isDeleted
                    ? 'bg-elevated-surface/30 text-muted italic'
                    : 'bg-bubble text-on-accent'
                }`
              : `rounded-[18px_18px_18px_4px] px-4 py-2.5 text-sm leading-relaxed shadow-[0_3px_10px_var(--color-shadow-subtle)] ${
                  message.isDeleted
                    ? 'bg-elevated-surface/30 text-muted italic'
                    : 'bg-elevated-surface text-on-elevated-surface'
                }`
          }
        >
          <span className="sr-only">{senderName}: </span>
          {message.content}
        </div>
        {isMine && onDelete && !message.isDeleted && (
          <button
            onClick={onDelete}
            aria-label="Delete message"
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
