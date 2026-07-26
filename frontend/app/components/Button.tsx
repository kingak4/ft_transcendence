import Link from 'next/link';
import type { ReactNode } from 'react';

type ButtonVariant = 'primary' | 'outline';

const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  primary: 'bg-primary text-on-primary hover:brightness-125',
  outline:
    'border border-primary text-primary hover:bg-primary hover:text-on-primary',
};

const BASE_CLASSES =
  'block w-full rounded-lg py-3 text-center text-sm font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-50';

type ButtonProps = {
  children: ReactNode;
  variant?: ButtonVariant;
  href?: string;
  type?: 'button' | 'submit';
  onClick?: () => void;
  disabled?: boolean;
};

export default function Button({
  children,
  variant = 'primary',
  href,
  type = 'button',
  onClick,
  disabled,
}: ButtonProps) {
  const className = `${BASE_CLASSES} ${VARIANT_CLASSES[variant]}`;

  if (href) {
    return (
      <Link href={href} className={className}>
        {children}
      </Link>
    );
  }

  return (
    <button
      type={type}
      className={className}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
}
