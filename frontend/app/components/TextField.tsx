import { type InputHTMLAttributes, forwardRef } from 'react';

type TextFieldTone = 'surface' | 'elevated' | 'chat';
type TextFieldSize = 'sm' | 'md';

// Which surface the field sits on. Mirrors the design tokens rather than
// inventing a parallel naming scheme, so the set of tones can only grow when
// the token set does. `chat` became expressible once the hub-* tokens landed.
//
// The focus ring lives here rather than in BASE_CLASSES because it varies by
// TODO(design-migration): `chat` is a third tone only because hub-* is a
// parallel palette. Once hub-* becomes *the* palette, `surface` and `chat`
// describe the same thing - collapse them and delete this tone rather than
// leaving three names for two appearances.
//
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
    'rounded-[14px] border border-elevated-border text-on-elevated-surface placeholder:text-on-elevated-surface/40 bg-elevated-surface shadow-[0_4px_14px_rgba(10,42,77,0.06)] focus:ring-primary',
  elevated:
    'rounded-xl text-on-elevated-surface placeholder:text-on-elevated-surface/40 bg-on-elevated-surface/10 focus:ring-primary',
  chat: 'rounded-lg text-hub-on-surface placeholder:text-hub-time bg-hub-field focus:ring-hub-blue',
};

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
