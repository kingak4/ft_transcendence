import Link from 'next/link';
import type { ComponentProps, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'outline' | 'send';

const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  primary: 'bg-primary text-on-primary hover:brightness-125',
  outline:
    'border border-primary text-primary hover:bg-primary hover:text-on-primary',
  // The chat composer's Send button. `brightness` is a filter, so it lightens
  // the rendered gradient; a hover:bg-* utility would flatten it to one colour.
  // TODO(stomp): the composer's Send needs a disabled state once it submits -
  // empty input and in-flight send. The base classes already style
  // `disabled:`, so this is a prop at the call site, not a change here.
  // TODO(design-migration): revisit whether `send` stays its own variant or
  // becomes `primary` once `primary` adopts bg-hub-cta. Keep it separate only
  // if the design really does treat Send differently from other CTAs.
  send: 'bg-hub-bubble text-white hover:brightness-125',
};

const BASE_CLASSES =
  'inline-block rounded-lg py-3 text-center text-sm font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-50';

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
  // Appearance is the component's; layout is the caller's. Width can't be left
  // to `className` because `w-full` and `w-auto` tie on specificity and resolve
  // by stylesheet order, not by the order they're written in the string.
  fullWidth?: boolean;
  className?: string;
};

type ButtonAsButton = BaseProps &
  Omit<ComponentProps<'button'>, 'className'> & { href?: never };

type ButtonAsLink = BaseProps & Omit<ComponentProps<typeof Link>, 'className'>;

export default function Button(props: ButtonAsButton | ButtonAsLink) {
  if (props.href !== undefined) {
    // `fullWidth` is destructured to keep it out of the spread as much as to
    // read it — an unknown `fullwidth` attribute on the DOM node would warn.
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

  // `href` is `undefined` on this branch, so leaving it in the spread costs
  // nothing — React omits undefined attributes.
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
