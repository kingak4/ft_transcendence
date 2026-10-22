import Link from 'next/link';
import type { ComponentProps, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'outline' | 'neutral';

const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  primary: 'rounded-xl bg-cta text-on-cta hover:brightness-125',
  outline:
    'rounded-xl border border-primary text-primary hover:bg-primary hover:text-on-primary',
  neutral:
    'rounded-xl bg-on-elevated-surface/10 text-on-elevated-surface/70 hover:bg-on-elevated-surface/20',
};

// TODO(stomp): the composer's Send needs a disabled state once it submits -
// empty input and in-flight send. The base classes already style `disabled:`,
// so this is a prop at the call site, not a change here.
const BASE_CLASSES =
  'inline-block py-3.5 text-center text-sm font-bold transition disabled:cursor-not-allowed disabled:opacity-50';

function buttonClasses(
  variant: ButtonVariant,
  fullWidth: boolean,
  extra?: string,
) {
  const width = fullWidth ? 'block w-full' : 'px-3';
  return [BASE_CLASSES, VARIANT_CLASSES[variant], width, extra]
    .filter(Boolean)
    .join(' ');
}

type BaseProps = {
  children: ReactNode;
  variant?: ButtonVariant;
  fullWidth?: boolean;
  className?: string;
};

type ButtonAsButton = BaseProps &
  Omit<ComponentProps<'button'>, 'className'> & { href?: never };

type ButtonAsLink = BaseProps & Omit<ComponentProps<typeof Link>, 'className'>;

export default function Button(props: ButtonAsButton | ButtonAsLink) {
  if (props.href !== undefined) {
    const {
      children,
      variant = 'primary',
      fullWidth = false,
      className,
      ...linkProps
    } = props;

    return (
      <Link
        {...linkProps}
        className={buttonClasses(variant, fullWidth, className)}
      >
        {children}
      </Link>
    );
  }

  const {
    children,
    variant = 'primary',
    fullWidth = false,
    className,
    type = 'button',
    ...buttonProps
  } = props;

  return (
    <button
      {...buttonProps}
      type={type}
      className={buttonClasses(variant, fullWidth, className)}
    >
      {children}
    </button>
  );
}
