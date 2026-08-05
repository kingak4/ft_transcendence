import Avatar from '../../components/Avatar';
import Composer from './Composer';
import MessageBubble from './MessageBubble';
import { CURRENT_USER_ID, type ChatMessage, type Friend } from './fixtures';

interface Props {
  friend: Friend;
  messages: ChatMessage[];
}

/**
 * Owns the scroll contract for the chat pane.
 *
 * A flex item defaults to `min-height: auto`, meaning it will not shrink below
 * its content. So `min-h-0` is required on *every* flex link between the page's
 * definite height (set in page.tsx) and the scrolling list - one missing link
 * and the list grows instead of scrolling, taking the whole page with it.
 *
 * TODO(stomp): the live message list has to become a Client Component to
 * subscribe. Split it out rather than marking this whole file `'use client'`,
 * so the header and the layout shell stay server-rendered.
 *
 * TODO(stomp): auto-scroll to the newest message on arrival, but only when the
 * user is already near the bottom - otherwise reading history yanks them away
 * mid-sentence every time someone types. Needs a ref on the scroll container.
 *
 * TODO(stomp): history is finite. Loading older messages on upward scroll
 * means preserving scroll position while prepending, or the view jumps.
 *
 * TODO(stomp): `useChat.ts` exposes a delete destination
 * (`/transcend/chat/messages/{messageId}/delete`) with no UI here yet.
 */
export default function Conversation({ friend, messages }: Props) {
  return (
    <section className="bg-hub-panel-sunken flex min-h-0 flex-1 flex-col">
      {/* Plain Avatar, not PresenceAvatar: the status label below the name
          already states presence, so a dot would say it twice. */}
      <header className="bg-hub-panel border-hub-border flex shrink-0 items-center gap-3 border-b px-5 py-4">
        <Avatar
          src={null}
          alt={friend.name}
          size={40}
          initial={friend.initial}
          color={friend.color}
        />
        <div className="flex flex-col">
          <h2 className="text-hub-on-surface text-sm font-bold">
            {friend.name}
          </h2>
          <span className="text-hub-status text-xs font-semibold">
            {friend.status}
          </span>
        </div>
      </header>

      {/* TODO(design-migration): add `overscroll-contain` here so reaching the
          end of this list stops handing the scroll to the page. Part of the
          footer-less (chat) route group fix - see the note in page.tsx.
          TODO(stomp): empty state - a conversation with no messages yet
          currently renders a blank pane with no explanation. */}
      <div className="flex min-h-0 flex-1 flex-col gap-4 overflow-y-auto p-6">
        {messages.map((message) => (
          <MessageBubble
            key={message.messageId}
            message={message}
            // Direction is derived, never stored: the wire format has no
            // `from` flag, only a senderId to compare against.
            isMine={message.senderId === CURRENT_USER_ID}
          />
        ))}
      </div>

      <Composer />
    </section>
  );
}
