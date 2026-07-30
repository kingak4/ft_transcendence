import type { InputHTMLAttributes } from 'react';

type TextFieldTone = 'surface' | 'elevated';
type TextFieldSize = 'sm' | 'md';

// Which surface the field sits on. Mirrors the design tokens rather than
// inventing a parallel naming scheme, so the set of tones can only grow when
// the token set does.
const TONE_CLASSES: Record<TextFieldTone, string> = {
  surface: 'text-on-surface placeholder:text-on-surface/40 bg-on-surface/10',
  elevated:
    'text-on-elevated-surface placeholder:text-on-elevated-surface/40 bg-on-elevated-surface/10',
};

const SIZE_CLASSES: Record<TextFieldSize, string> = {
  sm: 'px-3 py-2',
  md: 'px-4 py-3',
};

const BASE_CLASSES =
  'w-full rounded-lg text-sm outline-none focus:ring-1 focus:ring-primary';

// The native `size` attribute (a character count) is dropped so the name is
// free for the padding variant; no call site uses it. Every other input
// attribute passes straight through, keeping this component responsible for
// styling only.
type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> & {
  tone?: TextFieldTone;
  size?: TextFieldSize;
};

export default function TextField({
  tone = 'surface',
  size = 'md',
  className = '',
  ...inputProps
}: TextFieldProps) {
  // `className` is appended for *layout* only (margins, grid placement).
  // Passing colour or padding utilities here fights the classes above at equal
  // specificity and resolves by stylesheet order, not by this order.
  return (
    <input
      {...inputProps}
      className={`${BASE_CLASSES} ${TONE_CLASSES[tone]} ${SIZE_CLASSES[size]} ${className}`}
    />
  );
}
