import type { ReactNode } from 'react';

export default function Tag({ children }: { children: ReactNode }) {
  return (
    <span className="bg-primary text-on-primary rounded-full px-4 py-1.5 text-xs font-semibold">
      {children}
    </span>
  );
}
