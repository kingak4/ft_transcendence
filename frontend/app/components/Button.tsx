import Link from 'next/link';
import type { ComponentProps, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'outline';

const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  primary: 'bg-primary text-on-primary hover:brightness-125',
  outline:
    'border border-primary text-primary hover:bg-primary hover:text-on-primary',
};

const BASE_CLASSES =
  'block w-full rounded-lg py-3 text-center text-sm font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-50';

function buttonClasses(variant: ButtonVariant, extra?: string) {
  const classes = `${BASE_CLASSES} ${VARIANT_CLASSES[variant]}`;
  return extra ? `${classes} ${extra}` : classes;
}

/**
 * A styling wrapper must not narrow what callers can pass to the element it
 * wraps, so the props come from the element itself. `className` is owned here
 * (we merge into it) and therefore omitted from the forwarded set.
 *
 * The two shapes are mutually exclusive: `href` makes it a link, and
 * `href?: never` on the button shape stops `<Button href type="submit" />`
 * from type-checking as either one.
 */
type BaseProps = {
  children: ReactNode;
  variant?: ButtonVariant;
  className?: string;
};

type ButtonAsButton = BaseProps &
  Omit<ComponentProps<'button'>, 'className'> & { href?: never };

type ButtonAsLink = BaseProps & Omit<ComponentProps<typeof Link>, 'className'>;

export default function Button(props: ButtonAsButton | ButtonAsLink) {
  if (props.href !== undefined) {
    const { children, variant = 'primary', className, ...linkProps } = props;

    return (
      <Link {...linkProps} className={buttonClasses(variant, className)}>
        {children}
      </Link>
    );
  }

  // `href` is `undefined` on this branch, so leaving it in the spread costs
  // nothing — React omits undefined attributes.
  const {
    children,
    variant = 'primary',
    className,
    type = 'button',
    ...buttonProps
  } = props;

  return (
    <button
      {...buttonProps}
      type={type}
      className={buttonClasses(variant, className)}
    >
      {children}
    </button>
  );
}
