import Tag from './Tag';

const TAGS = [
  'Time tracking',
  'Task planning',
  'Progress tracking',
  'Stats',
  'Community',
];

// This was a fixed brand statement, wired to nothing themeable. It is now a
// mixture, and the mixture is deliberate but temporary:
//
//   fixed   - the gradient (`--theme-start-page-gradient-*` has no .mocha or
//             .latte override) and the body text, plain `white` for the same
//             reason the sidebar's on-rail text is fixed on its fixed gradient.
//   themed  - the two accent words, `text-primary`, which becomes Catppuccin
//             mauve under Mocha.
//
// The accents follow `primary` rather than the fixed `hub-lime` on purpose:
// `Tag` renders inside this card and took `bg-primary` at position 6, so if the
// two disagreed the pills and the highlighted words would drift apart in Mocha.
// Agreeing and both being odd beats disagreeing. Recorded in 12.5; position 21
// rebuilds this area and Step 5 decides whether the gradient themes at all.
//
// This file held the last four `brand-*` references in the component layer, so
// clearing them completes criterion 5 of §8.13 and unblocks Step 8's retirement
// of that palette. `Tag` cleared the other half at position 6.
//
// Typography now follows the dictionary's hero rows (12.0): headline 48px at
// 800 with -1px tracking, dropping to 30px narrow because 48px at 360 breaks
// into four lines; lead text 18px at 500. It was 36px/700 and 14px.
//
// Position 21 removed the card. The export's hero is not a box: it is a centred
// column sitting directly on the page's dark gradient, and that gradient now
// belongs to BareLayout, so carrying a second one here would have stacked the
// same background on itself. Widths follow the export's column - 720px for the
// stack, 600px for the headline, 520px for the lead - which is why the headline
// breaks where it does rather than running the full width.
//
// Still not reproduced: the 88px logo tile above the wordmark, and the wordmark
// itself. Both are part of the logo lockup logged in 12.5, which needs an SVG
// asset rather than a class.
export default function Hero() {
  return (
    <div className="mx-auto flex max-w-[720px] flex-col items-center gap-3.5 text-center">
      {/* The bottom margins are gone: the column above spaces its children with
          a 14px gap, and a gap does not replace margins - it adds to them. */}
      <h1 className="max-w-[600px] text-3xl font-extrabold leading-[1.1] tracking-[-1px] text-white lg:text-5xl">
        Every <span className="text-primary">skill</span> has a story –<br />
        start yours!
      </h1>
      <p className="max-w-[520px] text-lg font-medium leading-relaxed text-white/80">
        Turn your daily grind into a journey of mastery.
        <br />
        <span className="text-primary">42Hub</span> is a tool designed for
        high-achievers who want to bridge the gap between &quot;getting things
        done&quot; and &quot;getting better&quot;.
      </p>
      <div className="flex flex-wrap justify-center gap-2">
        {TAGS.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    </div>
  );
}
