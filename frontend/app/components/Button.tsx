import Link from 'next/link';
import type { ComponentProps, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'outline' | 'neutral';

// Radius sits here rather than in BASE_CLASSES, for the same reason it does in
// TextField: the export gives each treatment its own radius.
const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  // The export's CTA is a mint-to-lime gradient, which a single --theme-primary
  // cannot express - hence the bg-hub-cta utility rather than bg-primary.
  // `text-on-primary` is already the colour the export puts on it.
  //
  // Krok 6 unit 6.4 folded `send` into this variant. The export does draw two
  // different fills - Send takes the bubble's teal-to-blue, CTAs take
  // mint-to-lime - and 14.2 recorded "do not merge" as the default answer for
  // exactly that reason. That was overruled: one committing action, one
  // appearance. The consequence is deliberate and is NOT a refactor - the chat
  // composer's Send changes from teal-to-blue at 8px to mint-to-lime at 12px.
  // Logged in 14.5 as an intended difference rather than a failed probe, and it
  // is why this unit ships on its own commit.
  //
  // `bg-hub-bubble` survives, still drawing the outgoing message bubble it was
  // named for. Only the button that used it is gone.
  //
  // `brightness` is a filter, so it lightens the rendered gradient; a hover:bg-*
  // utility would flatten it to one colour.
  primary: 'rounded-xl bg-hub-cta text-on-primary hover:brightness-125',
  // No export reference: the design has no outline button on any route that
  // renders this component. Colours are therefore left as they were and only
  // the radius follows the component. See MIGRATION-INVENTORY.md 12.4.
  outline:
    'rounded-xl border border-primary text-primary hover:bg-primary hover:text-on-primary',
  // Krok 6 unit 6.6. There was no neutral treatment at all, which is the whole
  // reason the avatar modal hand-rolled its own pair of buttons (unit 6.7): its
  // Save could use `primary`, its Cancel had nothing to use. The values are that
  // Cancel's, promoted rather than invented - a translucent wash of the surface
  // foreground, which needs no new token and works on any elevated surface.
  neutral:
    'rounded-xl bg-on-elevated-surface/10 text-on-elevated-surface/70 hover:bg-on-elevated-surface/20',
};

// TODO(stomp): the composer's Send needs a disabled state once it submits -
// empty input and in-flight send. The base classes already style `disabled:`,
// so this is a prop at the call site, not a change here.
//
// Padding was frozen by /chat while `send` shared these classes: 12.5 recorded
// at position 1 that the export puts 14px here and we render 12px. Unit 6.4
// unblocked it - /chat's Send is being restyled by that unit anyway, so there is
// no longer an appearance to protect - and `py-3.5` is written exactly, because
// 12.0 treats spacing as a dynamic scale that needs no rounding.
//
// Type size and weight are NOT taken with it. The export asks for 15px at 800;
// 15 falls exactly between `text-sm` and `text-base`, so 12.0's tie-break sends
// it to 16px, and a jump from 14 to 16 on every button in the app is a design
// call rather than the mechanical unfreezing this is. Left open in 14.2.
const BASE_CLASSES =
  'inline-block py-3.5 text-center text-sm font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-50';

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
