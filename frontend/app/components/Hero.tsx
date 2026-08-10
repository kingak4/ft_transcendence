// This was a fixed brand statement, wired to nothing themeable. Step 4 left it
// a MIXTURE - fixed gradient and fixed white text, themed accent words - and
// recorded that as deliberate but temporary, pending Step 5's decision on
// whether the gradient themes at all.
//
// Step 5 decided that it does, so the mixture is gone: the gradient now takes
// .mocha/.latte overrides via BareLayout, and the two `white` literals here
// became `on-start-page`. They were only ever shorthand for "the landing is
// dark", which stopped being true under Latte.
//
// The accents still follow `primary` rather than `hub-lime`, and that reasoning
// survives unchanged: `Tag` renders inside this column and took `bg-primary` at
// position 6, so if the two disagreed the pills and the highlighted words would
// drift apart. What HAS changed is that they no longer drift apart from the
// background either.
//
// Alpha, not opacity, on the lead paragraph - it wraps a `text-primary` span,
// and `opacity` would compound down onto that accent while colour alpha leaves
// it alone. Same rule as login/page.tsx.
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
      <h1 className="text-on-start-page max-w-[600px] text-3xl font-extrabold leading-[1.1] tracking-[-1px] lg:text-5xl">
        Let&apos;s chat with your friends.
      </h1>
      <p className="text-on-start-page/80 max-w-[520px] text-lg font-medium leading-relaxed">
       Join 42Hub to stay close to the people you care about — real conversations, made simple.
      </p>
    </div>
  );
}
