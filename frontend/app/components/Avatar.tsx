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
        // Fallback colours follow the export's profile avatar: #0d2b47 on
        // #a3e635, which are exactly --theme-on-surface and --theme-primary,
        // so this needs no new tokens. The previous pairing was white on
        // bg-elevated-border (#eef2ef) - an initial that was very nearly
        // invisible whenever no per-user colour was supplied.
        className="bg-on-surface text-primary flex shrink-0 items-center justify-center rounded-full font-extrabold"
        // Inline styles outrank stylesheet rules, so `color` overrides
        // bg-on-surface when given and leaves it in place when not.
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
