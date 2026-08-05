import type { ChatMessage } from './fixtures';

interface Props {
  message: ChatMessage;
  /**
   * Derived by the caller from `senderId`, never read off the message itself -
   * the wire format carries no direction flag.
   */
  isMine: boolean;
  /*
   * TODO(stomp): real messages need delivery state (sending / sent / failed)
   * and a delete affordance for `isMine` bubbles. Both are per-message
   * presentation, so they belong here rather than in Conversation.
   *
   * TODO(stomp): `time` is a pre-formatted display string from the backend.
   * If that ever becomes a timestamp, format it in the user's locale and
   * timezone - and render it inside a <time dateTime={...}> element, which is
   * why it is a plain <span> today: <time> requires a machine-readable value.
   */
}

export default function MessageBubble({ message, isMine }: Props) {
  return (
    // `self-end` / `self-start` are flex *logical* alignments, so they already
    // follow writing direction and cost nothing extra in RTL.
    <div
      className={`flex max-w-[420px] flex-col ${isMine ? 'self-end' : 'self-start'}`}
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
      <div
        className={
          isMine
            ? 'bg-hub-bubble rounded-[18px_18px_4px_18px] px-4 py-2.5 text-sm leading-relaxed text-white'
            : 'bg-hub-panel text-hub-ink rounded-[18px_18px_18px_4px] px-4 py-2.5 text-sm leading-relaxed shadow-[0_3px_10px_rgba(10,42,77,0.06)]'
        }
      >
        {message.content}
      </div>
      <span
        className={`text-hub-time mt-1 text-[11px] font-semibold ${isMine ? 'text-end' : 'text-start'}`}
      >
        {message.time}
      </span>
    </div>
  );
}
