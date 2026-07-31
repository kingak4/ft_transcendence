import type { ChatMessage } from './types';

interface Props {
  message: ChatMessage;
  /**
   * Derived by the caller from `senderId`, never read off the message itself -
   * the wire format carries no direction flag.
   */
  isMine: boolean;
  onDelete?: () => void;
  /*
   * TODO(stomp): real messages need delivery state (sending / sent / failed).
   * Both are per-message presentation, so they belong here rather than in Conversation.
   */
}

export default function MessageBubble({ message, isMine, onDelete }: Props) {
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
    // `self-end` / `self-start` are flex *logical* alignments, so they already
    // follow writing direction and cost nothing extra in RTL.
    <div
      className={`group flex max-w-[420px] flex-col ${isMine ? 'self-end' : 'self-start'}`}
    >
      <div className={`flex items-center gap-2 ${isMine ? 'flex-row-reverse' : 'flex-row'}`}>
        {/*
         * The asymmetric radius encodes direction: the 4px corner is the tail,
         * pointing back at whoever sent it. These four corners are the one
         * physical (non-logical) value on this page; the design export mirrors
         * them for RTL, which is deferred with the rest of i18n.
         *
         * TODO(design-migration): when RTL (#86) lands, mirror these four corners.
         * Everything else here is already direction-agnostic (self-start/end,
         * text-start/end), so this is the only bit that needs an `rtl:` variant.
         */}
        <div
          className={
            isMine
              ? `rounded-[18px_18px_4px_18px] px-4 py-2.5 text-sm leading-relaxed ${
                  message.isDeleted ? 'bg-hub-panel/30 text-hub-muted italic' : 'bg-hub-bubble text-white'
                }`
              : `rounded-[18px_18px_18px_4px] px-4 py-2.5 text-sm leading-relaxed shadow-[0_3px_10px_rgba(10,42,77,0.06)] ${
                  message.isDeleted ? 'bg-hub-panel/30 text-hub-muted italic' : 'bg-hub-panel text-hub-ink'
                }`
          }
        >
          {message.content}
        </div>
        {isMine && onDelete && !message.isDeleted && (
          <button
            onClick={onDelete}
            aria-label="Delete message"
            className="text-hub-muted hover:text-red-500 opacity-0 transition-opacity group-hover:opacity-100 p-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 6h18"></path><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path></svg>
          </button>
        )}
      </div>
      <time
        dateTime={message.time}
        className={`text-hub-time mt-1 text-[11px] font-semibold ${isMine ? 'text-end' : 'text-start'}`}
      >
        {formatTime(message.time)}
      </time>
    </div>
  );
}
