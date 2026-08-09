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
  // The export separates sections with a rule, not with a gap: each one is
  // `padding: 22px 0` under a 1px #eef2ef top border, its children spaced 10px
  // apart. So `mb-8` does not become a smaller margin - it goes away, and the
  // border plus symmetric padding does the same job with a visible boundary.
  //
  // The heading takes its dictionary row (12.0, "nagłówek sekcji w treści"):
  // 18px at 800, down from 24px at 600. Its colour does NOT follow the export.
  // #155e8f is not any token we have - not hub-blue (#2f9bcf), not hub-teal
  // (#146b7a) - and Step 4 may not add one (§8.7 reguła 2), so the heading
  // inherits the card's on-elevated-surface instead. Recorded in 12.4.
  return (
    <section className="border-elevated-border py-5.5 flex flex-col gap-2.5 border-t">
      <h2 className="text-lg font-extrabold">{title}</h2>
      {children}
    </section>
  );
}
