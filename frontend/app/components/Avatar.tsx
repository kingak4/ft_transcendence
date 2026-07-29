import Image from 'next/image';

interface Props {
  src: string | null;
  alt: string;
  size: number;
}

export default function Avatar({ src, alt, size }: Props) {
  if (!src) {
    return (
      <div
        className="shrink-0 rounded-full bg-elevated-border"
        style={{ width: size, height: size }}
      />
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
