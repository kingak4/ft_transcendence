import { type InputHTMLAttributes, forwardRef } from 'react';

type TextFieldTone = 'surface' | 'card' | 'chat';
type TextFieldSize = 'sm' | 'md';

const TONE_CLASSES: Record<TextFieldTone, string> = {
  surface:
    'rounded-[14px] border border-elevated-edge text-on-elevated-surface placeholder:text-on-elevated-surface/40 bg-elevated-surface font-medium shadow-[0_4px_14px_var(--color-shadow-subtle)] focus:ring-primary',
  card: 'rounded-xl border border-card-field-border text-on-card placeholder:text-on-card/40 bg-card-field font-medium focus:ring-primary',
  chat: 'rounded-lg text-on-elevated-surface placeholder:text-timestamp bg-field focus:ring-accent-blue',
};

const SIZE_CLASSES: Record<TextFieldSize, string> = {
  sm: 'px-3 py-2',
  md: 'px-4 py-3',
};

const BASE_CLASSES = 'w-full text-sm outline-none focus:ring-1';

type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> & {
  tone?: TextFieldTone;
  size?: TextFieldSize;
};

const TextField = forwardRef<HTMLInputElement, TextFieldProps>(
  function TextField(
    { tone = 'surface', size = 'md', className = '', ...inputProps },
    ref,
  ) {
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
