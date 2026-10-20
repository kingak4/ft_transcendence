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
  initial?: string;
  color?: string;
}

export default function Avatar({ src, alt, size, initial, color }: Props) {
  if (!src) {
    return (
      <div
        aria-hidden="true"
        className="bg-primary text-on-primary flex shrink-0 items-center justify-center rounded-full font-extrabold"
        style={{
          width: size,
          height: size,
          backgroundColor: color,
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
