import Link from 'next/link';

interface Props {
  className?: string;
}

export default function BrandLink({ className }: Props) {
  return (
    <Link
      href="/"
      className={`text-3xl font-bold italic${className ? ` ${className}` : ''}`}
    >
      42Hub
    </Link>
  );
}
