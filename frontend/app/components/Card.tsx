import type { ReactNode } from 'react';

export default function Card({ children }: { children: ReactNode }) {
  return (
    <div className="bg-elevated-surface text-on-elevated-surface border-elevated-border w-72 rounded-2xl border p-8">
      {children}
    </div>
  );
}
