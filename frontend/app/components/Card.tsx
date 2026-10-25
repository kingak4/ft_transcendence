import type { ReactNode } from 'react';

export default function Card({ children }: { children: ReactNode }) {
  return (
    <div className="bg-card border-card-border text-on-card w-full max-w-[440px] rounded-3xl border p-6 shadow-[0_25px_60px_rgba(0,0,0,0.35)] backdrop-blur-[18px] lg:p-9">
      {children}
    </div>
  );
}
