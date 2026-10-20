import { ICON_PATHS, type IconName } from './icons';

type IconSize = 'xs' | 'sm' | 'md' | 'lg';

const SIZE_PX: Record<IconSize, number> = {
  xs: 12,
  sm: 16,
  md: 20,
  lg: 24,
};

interface Props {
  name: IconName;
  size?: IconSize;
  // Present = meaningful icon announced to screen readers; absent = decorative.
  label?: string;
  className?: string;
}

export default function Icon({ name, size = 'md', label, className }: Props) {
  const px = SIZE_PX[size];
  const a11y = label
    ? { role: 'img', 'aria-label': label }
    : { 'aria-hidden': true };

  return (
    <svg
      {...a11y}
      xmlns="http://www.w3.org/2000/svg"
      width={px}
      height={px}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
    >
      {ICON_PATHS[name]}
    </svg>
  );
}
