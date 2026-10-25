import type { ReactNode } from 'react';

export default function LegalSection({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <section className="border-elevated-border py-5.5 flex flex-col gap-2.5 border-t">
      <h2 className="text-on-elevated-surface-accent text-lg font-extrabold">
        {title}
      </h2>
      {children}
    </section>
  );
}
