import type { ReactNode } from 'react';

export default function Card({ children }: { children: ReactNode }) {
  return (
    <div className="bg-inverse-surface text-on-inverse-surface w-72 rounded-2xl p-8">
      {children}
    </div>
  );
}
