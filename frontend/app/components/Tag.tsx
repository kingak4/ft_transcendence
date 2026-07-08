import type { ReactNode } from 'react';

// Brand-locked like the landing page hero it appears in - intentionally
// not wired to the surface/primary theme tokens.
export default function Tag({ children }: { children: ReactNode }) {
  return (
    <span className="bg-brand-additional-color text-brand-additional-color-2 rounded-full px-4 py-1.5 text-xs font-bold">
      {children}
    </span>
  );
}
