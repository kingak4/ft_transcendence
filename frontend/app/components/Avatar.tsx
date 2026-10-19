import Image from 'next/image';

interface Props {
  src: string | null;
  alt: string;
  size: number;
  // TODO(stomp): fixtures hand these in ready-made. Real users carry neither -
  // derive `initial` from the display name and `color` deterministically from
  // the user id (hash -> palette index) so a given person is always the same
  // colour across sessions and devices. Put that helper next to this
  // component; picking a fallback appearance is already its concern.
  //
  // Fallback identity, read only when `src` is absent. Both are optional, so
  // callers that pass neither keep today's plain circle. Picking what to show
  // in place of a missing image is already this component's job, so this
  // extends its responsibility rather than adding a second one.
  //
  // Krok 6 unit 6.10 asked whether this is dead code, since the backend issues
  // `avatarId` to every user and `src` is therefore never null. KEPT, as a
  // guard: "user without an avatar" is not the only way in - a failed avatar
  // fetch lands here too - and the TODO above commits to deriving initials and
  // colours for real users, so deleting it now means writing it again shortly.
  // Recorded in 14.2.
  //
  // `color` IS being misused today: the chat routes pass `bg-hub-panel`, a
  // class name, into what becomes an inline `backgroundColor`, where it is
  // simply ignored. That is a data bug rather than a styling one - logged in
  // 14.3, not fixed here.
  initial?: string;
  color?: string;
}

export default function Avatar({ src, alt, size, initial, color }: Props) {
  if (!src) {
    return (
      <div
        // Hidden from assistive tech: the initial duplicates the name that
        // sits next to it, and this branch carries no accessible name today.
        aria-hidden="true"
        // Krok 7 unit 7.3: `primary` / `on-primary`, the pair Krok 5 tuned per
        // flavour (13.4.5). The export's profile avatar is the same two colours
        // the other way round - a lime initial on navy - and that was what this
        // drew, as `bg-on-surface text-primary`. But those are two unrelated
        // roles that only happened to contrast in 42Hub; under Mocha and Latte
        // they resolved to text-on-mauve at roughly 1-2:1. Inverting it in
        // 42Hub was accepted so that the pairing holds in every flavour.
        className="bg-primary text-on-primary flex shrink-0 items-center justify-center rounded-full font-extrabold"
        // Inline styles outrank stylesheet rules, so `color` overrides
        // bg-primary when given and leaves it in place when not.
        style={{
          width: size,
          height: size,
          backgroundColor: color,
          // The export holds this ratio steady across every avatar it draws:
          // 34/96 on the profile hero, 15/44 in list rows, 14/42 in the chat
          // header - all ~0.34. The previous 0.4 ran visibly large.
          fontSize: Math.round(size * 0.34),
        }}
      >
        {initial}
      </div>
    );
  }

  return (
    <Image
      src={src}
      alt={alt}
      width={size}
      height={size}
      unoptimized
      className="shrink-0 rounded-full object-cover"
      style={{ width: size, height: size }}
    />
  );
}
