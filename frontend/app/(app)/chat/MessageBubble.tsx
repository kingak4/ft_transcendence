import type { ChatMessage } from './types';
import Link from 'next/link';

interface Props {
  message: ChatMessage;
  /**
   * Derived by the caller from `senderId`, never read off the message itself -
   * the wire format carries no direction flag.
   */
  isMine: boolean;
  onDelete?: () => void;
  /*
   * TODO(stomp): Real messages need delivery state (sending / sent / failed).
   * This requires the backend to accept and echo a `clientMessageId` (correlation ID)
   * so the frontend can reconcile optimistic messages reliably.
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
      <div
        className={`flex items-center gap-2 ${isMine ? 'flex-row-reverse' : 'flex-row'}`}
      >
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
        {/*
         * Two Step 5 corrections, both of the same kind - a colour that was
         * only ever correct while the chat was a fixed-light island:
         *
         * - the outgoing bubble was `text-white` on the teal-to-blue fill.
         *   That fill now themes, so the label takes `hub-on-accent`, the role
         *   that means "legible on an accent fill" and inverts with it.
         * - the incoming bubble was `text-hub-ink` on `bg-hub-panel`. This one
         *   was a genuine bug rather than a tightening: `hub-ink` describes the
         *   CONTRAST PARTNER OF A LIGHT ACCENT FILL, and under Mocha it
         *   resolves to crust while `hub-panel` resolves to surface0 - dark on
         *   dark, unreadable. Text sitting on a panel wants the panel's own
         *   foreground, which is `hub-on-surface`.
         *
         * The second is why 13.6.2 exists: the role name read plausibly at the
         * call site and only the resolved pair showed the problem.
         */}
        <div
          className={
            isMine
              ? `rounded-[18px_18px_4px_18px] px-4 py-2.5 text-sm leading-relaxed ${
                  message.isDeleted
                    ? 'bg-hub-panel/30 text-hub-muted italic'
                    : 'bg-hub-bubble text-hub-on-accent'
                }`
              : `rounded-[18px_18px_18px_4px] px-4 py-2.5 text-sm leading-relaxed shadow-[0_3px_10px_rgba(10,42,77,0.06)] ${
                  message.isDeleted
                    ? 'bg-hub-panel/30 text-hub-muted italic'
                    : 'bg-hub-panel text-hub-on-surface'
                }`
          }
        >
          {(() => {
            const gameInviteMatch = message.content.match(/^#game-invite:([a-zA-Z0-9_-]+)#$/);
            const isGameInvite = !!gameInviteMatch && !message.isDeleted;
            const gameName = gameInviteMatch ? gameInviteMatch[1] : '';

            if (isGameInvite) {
              return (
                <div className="flex flex-col gap-1">
                  <span className="font-semibold text-sm">Wyzwanie: {gameName} 🎮</span>
                  <p className="text-[11px] opacity-80 mb-1">
                    {isMine ? 'Wysłałeś zaproszenie do gry.' : 'Zaprasza Cię do wspólnej gry!'}
                  </p>
                  <Link
                    href={`/games/${gameName}`}
                    className={`mt-1 inline-block rounded-md px-3 py-1.5 text-center text-xs font-bold transition-colors ${
                      isMine
                        ? 'bg-black/20 text-hub-on-accent hover:bg-black/30 dark:bg-white/20 dark:hover:bg-white/30'
                        : 'bg-hub-bubble text-hub-on-accent hover:opacity-90'
                    }`}
                  >
                    Zagraj w {gameName}
                  </Link>
                </div>
              );
            }
            return message.content;
          })()}
        </div>
        {isMine && onDelete && !message.isDeleted && (
          <button
            onClick={onDelete}
            aria-label="Delete message"
            className="text-hub-muted hover:text-danger p-1 opacity-0 transition-opacity group-hover:opacity-100"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M3 6h18"></path>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
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
