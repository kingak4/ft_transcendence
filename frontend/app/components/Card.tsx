import type { ReactNode } from 'react';

// Radius and padding follow the export's large card (24px, 36px). Padding is
// the first responsive value in the migration: 12.0 gives large cards 24px on
// narrow screens, because the card is nearly the full width of the viewport
// there and its own padding has to give way to its contents. Written
// mobile-first, so the unprefixed value is the narrow one and `lg:` (1024px)
// describes the wide screen.
//
// Position 22 finally supplies what position 4 deferred. Both halves had to land
// together: this card's four call sites all set their own text in
// `on-elevated-surface`, so darkening the surface without recolouring them would
// have produced dark text on a dark card.
//
// The token itself is untouched. `on-elevated-surface` still paints UserList
// rows, the legal-page card and TextField's `surface` tone, all on light (app)
// routes; only `Card` and its consumers move, and every one of them lives under
// BareLayout, which position 21 made dark.
//
// `w-72` is gone. The export wants `width:100%; max-width:440px`, which position
// 4 could not use because the landing put this card in a flex-wrap row beside
// `Hero` and a full-width card forced a line break. Position 21 replaced that
// row with a centred stack, so the constraint went with it.
//
// The background gradient is written as an arbitrary value: no Tailwind scale
// expresses a two-stop translucent dark gradient, the same reason 12.0 keeps
// shadows exact. The border and shadow ARE dictionary rows.
export default function Card({ children }: { children: ReactNode }) {
  return (
    <div className="w-full max-w-[440px] rounded-3xl border border-white/[0.14] bg-[linear-gradient(160deg,rgba(20,40,45,0.55),rgba(15,26,38,0.6))] p-6 text-white shadow-[0_25px_60px_rgba(0,0,0,0.35)] backdrop-blur-[18px] lg:p-9">
      {children}
    </div>
  );
}
