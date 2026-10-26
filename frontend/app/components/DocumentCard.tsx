import type { ReactNode } from 'react';

export default function DocumentCard({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <div className="bg-elevated-surface text-on-elevated-surface-body mx-auto flex max-w-[720px] flex-col gap-1.5 rounded-3xl p-6 text-base font-medium leading-relaxed shadow-[0_8px_24px_var(--color-shadow-card)] lg:px-14 lg:py-12">
      <h1 className="text-on-elevated-surface text-2xl font-extrabold tracking-tight lg:text-3xl">
        {title}
      </h1>
      {children}
    </div>
  );
}
