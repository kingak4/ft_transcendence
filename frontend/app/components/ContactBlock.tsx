// One source of truth for the project's postal contact details — every legal
// page is required to show the same address, so a change to one is a change
// to all of them.
const CONTACT_LINES = [
  'ft_transcendence',
  '42 Warsaw',
  'Al. Solidarności 171B',
  '00-877 Warszawa',
  'Poland',
  'Email: kinga.kwasniak5@gmail.com',
];

// `className` is a layout escape hatch only — the caller owns the spacing
// between this block and whatever precedes it, not its internals.
export default function ContactBlock({
  className = '',
}: {
  className?: string;
}) {
  // 10px, not 8px, so the rhythm is continuous with `LegalSection`. In the export
  // these lines are not a block at all - they are plain paragraphs of the contact
  // section, spaced by that section's own `gap: 10px`. Our wrapper exists for the
  // shared constant and the className hatch, so it has to reproduce that gap
  // itself; leaving it at 8px puts a seam under the wrapper that the design has
  // no reason to show.
  return (
    <div className={`space-y-2.5 ${className}`}>
      {CONTACT_LINES.map((line) => (
        <p key={line}>{line}</p>
      ))}
    </div>
  );
}
