import type { ReactNode } from 'react';

// The numbered `section` + `h2` pair repeated by every legal page. Section
// numbers stay in the `title` string: the pages don't share a numbering
// sequence (privacy ends at 8, terms at 9), so deriving them automatically
// would couple two documents that are only formatted alike.
export default function LegalSection({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <section className="mb-8">
      <h2 className="mb-4 text-2xl font-semibold">{title}</h2>
      {children}
    </section>
  );
}
