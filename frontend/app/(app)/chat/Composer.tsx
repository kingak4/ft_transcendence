import Button from '../../components/Button';
import TextField from '../../components/TextField';

/**
 * Inert on this branch: no handlers, no submit. Wiring this up means making it
 * a Client Component and calling the STOMP send destination
 * `/transcend/chat/{chatId}/send` with `{ content }`.
 *
 * TODO(stomp): the full list, in the order it bites:
 *   1. `'use client'` here (and only here, not up the tree), take `chatId` as
 *      a prop, publish `{ content }` to the destination above.
 *   2. Wrap in a <form> with onSubmit so Enter sends and the button is
 *      `type="submit"` - right now Enter does nothing, which reads as broken.
 *   3. Controlled input, cleared on send.
 *   4. Disable the button on empty/whitespace input and while a send is in
 *      flight; the base Button styles already cover `disabled:`. See the
 *      matching note in Button.tsx about why it is enabled today.
 *   5. Handle send failure - the socket can be down. A message that silently
 *      vanishes is worse than a visible error.
 *   6. Decide on optimistic rendering: showing the message immediately needs a
 *      temporary id reconciled against the server's `messageId` on echo.
 */
export default function Composer() {
  return (
    <div className="bg-hub-panel border-hub-border flex shrink-0 items-center gap-3 border-t p-4">
      {/* `className` here is layout only, which is what TextField permits;
          colour and padding stay owned by `tone` and `size`. */}
      <TextField
        tone="chat"
        placeholder="Type a message"
        aria-label="Message"
        className="flex-1"
      />
      {/* Deliberately not `disabled`: the base button styles dim disabled
          buttons to 50% opacity, which would misrepresent the gradient on a
          page whose job is to show the design. */}
      <Button variant="send">Send</Button>
    </div>
  );
}
