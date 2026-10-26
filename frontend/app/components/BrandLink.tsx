import Link from 'next/link';

interface Props {
  className?: string;
}

export default function BrandLink({ className }: Props) {
  return (
    <Link
      href="/"
      className={`text-xl font-extrabold${className ? ` ${className}` : ''}`}
    >
      JustPlay42
    </Link>
  );
}
