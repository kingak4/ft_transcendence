import Link from 'next/link';

interface Props {
  className?: string;
}

// The export carries the wordmark four times and never once in italic: landing
// hero (26px/800/#fff), both legal-page headers (19px/800/#0d2b47) and the
// sidebar (19px/800/#fff). Dropping `italic` is therefore not a reading of the
// export so much as the removal of something the export never had.
//
// 19px is the *chrome* size - sidebar and legal header, both a mark set beside a
// logo tile. Our two call sites are exactly that: `absolute left-6 top-6` in
// `BareLayout` and the sidebar header. The export's 26px belongs to its centred
// hero wordmark stacked under an 88px tile, a lockup we do not reproduce; if the
// landing ever wants it, that is `Hero`'s call (pozycja 19). 19 sits midway
// between `text-lg` (18) and `text-xl` (20), and the tie-break in 12.0 rounds up.
//
// Colour stays at the call site on purpose. The export genuinely varies it by
// background - white on the dark sidebar and landing, #0d2b47 on the white legal
// header - so it is a property of the placement, not of the mark. `Sidebar`
// passing `text-white` is correct; `BareLayout` passing nothing is the loose end
// (12.5).
//
// A size prop would express the 26/19 split honestly, but that is an API change
// (§8.7 reguła 3) and belongs to Krok 6.
export default function BrandLink({ className }: Props) {
  return (
    <Link
      href="/"
      className={`text-xl font-extrabold${className ? ` ${className}` : ''}`}
    >
      42Hub
    </Link>
  );
}
