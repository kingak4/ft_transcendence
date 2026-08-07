import type { ReactNode } from 'react';

// Radius and padding follow the export's large card (24px, 36px). Padding is
// the first responsive value in the migration: 12.0 gives large cards 24px on
// narrow screens, because the card is nearly the full width of the viewport
// there and its own padding has to give way to its contents. Written
// mobile-first, so the unprefixed value is the narrow one and `lg:` (1024px)
// describes the wide screen.
//
// Colours, shadow and width are deliberately NOT here yet:
// - colours and shadow wait for the routes that supply the background
//   (decision (2) in MIGRATION-INVENTORY.md 12.4). A dark card now would put
//   dark text on a dark surface for the rest of Faza 1.
// - `w-72` stays because the export's `max-width:440px` assumes a centred
//   column; the landing page puts this card in a flex-wrap row beside `Hero`,
//   where `w-full` would force a line break. That is a call-site decision.
export default function Card({ children }: { children: ReactNode }) {
  return (
    <div className="bg-elevated-surface text-on-elevated-surface border-elevated-border w-72 rounded-3xl border p-6 lg:p-9">
      {children}
    </div>
  );
}
