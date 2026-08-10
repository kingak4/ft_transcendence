import { type InputHTMLAttributes, forwardRef } from 'react';

type TextFieldTone = 'surface' | 'card' | 'chat';
type TextFieldSize = 'sm' | 'md';

// Which surface the field sits on. Mirrors the design tokens rather than
// inventing a parallel naming scheme, so the set of tones can only grow when
// the token set does. `chat` became expressible once the hub-* tokens landed.
//
// Krok 6 resolved the two questions this block used to defer.
//
// 6.1 - the TODO here predicted that once hub-* became *the* palette, `surface`
// and `chat` would describe the same thing and could collapse. Krok 5 made the
// palette single and they did NOT converge, because they were never one
// appearance described twice: `surface` sits ON the page and lifts to
// ctp-surface0, `chat` sits IN a panel and drops to ctp-base. Two elevations,
// two treatments - radius, border, shadow and focus ring all differ for the
// same reason. Merging them would have restyled /chat, which is the silent
// redesign §14.0 exists to prevent. Kept, with the reason in 14.2.
//
// 6.3 - the third tone WAS misnamed, though. It was `elevated`, taken from
// --theme-elevated-surface, which is white - while the tone it named is the
// dark auth card. Krok 5 pointed it at the hub-card-* tokens and made the
// contradiction worse, so this is the rename: it is `card`, after the surface
// it actually sits on. Appearance is untouched.
//
// The focus ring lives here rather than in BASE_CLASSES because it varies by
// tone: `focus:ring-primary` and `focus:ring-hub-blue` carry equal specificity,
// so a tone could not have overridden a base ring reliably - the cascade would
// have resolved it by stylesheet order. Both pre-existing tones keep the ring
// they always had, so their rendered class set is unchanged.
//
// Radius moved here from BASE_CLASSES in Step 4 of the design migration, for
// the same reason the ring is here: the export gives each appearance its own
// radius (12px on the auth field, 14px on the search field), while `chat` must
// keep the 8px it renders today - /chat is the reference implementation and is
// out of scope for this step. One shared base radius could not satisfy both.
// Padding is still shared via SIZE_CLASSES and so is still frozen by /chat;
// see MIGRATION-INVENTORY.md 12.5.
const TONE_CLASSES: Record<TextFieldTone, string> = {
  surface:
    'rounded-[14px] border border-elevated-border text-on-elevated-surface placeholder:text-on-elevated-surface/40 bg-elevated-surface font-medium shadow-[0_4px_14px_rgba(10,42,77,0.06)] focus:ring-primary',
  // Position 22 gave this tone the export's `inputStyle`: white text on
  // `white/8`, a `white/15` hairline, 14.5px at 500. It had to change in the
  // same commit as `Card` - it sits only on the auth card, and the dark text it
  // carried before would have been invisible the moment that card darkened.
  //
  // The weight is here rather than in BASE_CLASSES on purpose: 12.5 recorded at
  // position 1 that the export puts 500 on its fields and we render 400, and it
  // is settable per tone, so `/chat` does not block it. `surface` is the other
  // field the export weights at 500; `chat` keeps what it renders today.
  //
  // Step 5 unit 5.4 replaced the four white literals with tokens. The export's
  // ratios are unchanged - the fill is still 8% of the foreground and the
  // hairline still 15% - but "the foreground is white" stopped being true once
  // this card could render under Latte, so the relationship is expressed
  // against a role instead of against a colour. Krok 6 unit 6.3 then renamed it
  // from `elevated` to `card`: it took its name from --theme-elevated-surface,
  // which is white, while describing the dark auth card, and pointing it at the
  // hub-card-* tokens made that mismatch worse rather than better.
  card: 'rounded-xl border border-hub-card-field-border text-hub-on-card placeholder:text-hub-on-card/40 bg-hub-card-field font-medium focus:ring-primary',
  chat: 'rounded-lg text-hub-on-surface placeholder:text-hub-time bg-hub-field focus:ring-hub-blue',
};

// 6.2 - the export draws FOUR field appearances against these three tones: its
// search field is 15x18px where our `md` is 16x12px. That gap is PADDING, and
// padding lives here rather than in a tone, so a fourth tone would not have
// closed it. It stays shared and stays frozen by /chat, because 6.1 kept `chat`
// rather than merging it away. Recorded in 14.2; the field is logged in 12.5.
const SIZE_CLASSES: Record<TextFieldSize, string> = {
  sm: 'px-3 py-2',
  md: 'px-4 py-3',
};

const BASE_CLASSES = 'w-full text-sm outline-none focus:ring-1';

// The native `size` attribute (a character count) is dropped so the name is
// free for the padding variant; no call site uses it. Every other input
// attribute passes straight through, keeping this component responsible for
// styling only.
type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> & {
  tone?: TextFieldTone;
  size?: TextFieldSize;
};

const TextField = forwardRef<HTMLInputElement, TextFieldProps>(
  function TextField(
    { tone = 'surface', size = 'md', className = '', ...inputProps },
    ref,
  ) {
    // `className` is appended for *layout* only (margins, grid placement).
    // Passing colour or padding utilities here fights the classes above at equal
    // specificity and resolves by stylesheet order, not by this order.
    return (
      <input
        ref={ref}
        {...inputProps}
        className={`${BASE_CLASSES} ${TONE_CLASSES[tone]} ${SIZE_CLASSES[size]} ${className}`}
      />
    );
  },
);

export default TextField;
