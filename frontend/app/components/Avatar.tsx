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
        className="bg-elevated-border flex shrink-0 items-center justify-center rounded-full font-bold text-white"
        // Inline styles outrank stylesheet rules, so `color` overrides
        // bg-elevated-border when given and leaves it in place when not.
        style={{
          width: size,
          height: size,
          backgroundColor: color,
          fontSize: Math.round(size * 0.4),
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
      className="shrink-0 rounded-full object-cover"
      style={{ width: size, height: size }}
    />
  );
}
