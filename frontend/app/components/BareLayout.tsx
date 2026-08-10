import type { ReactNode } from 'react';

import BrandLink from './BrandLink';
import Footer from './Footer';

interface Props {
  children: ReactNode;
}

// Shell for routes that shouldn't show the Sidebar (login, register, root) -
// just the brand mark, the page content, and the footer.
//
// Position 21 moved this off `surface` and onto the landing gradient. It lives
// here rather than in either page because BareLayout is what `(marketing)` and
// `(auth)` share, and decision (2) puts the same background behind both; a
// gradient written twice is a gradient that will disagree with itself.
//
// `text-white` is the other half. Two things were inheriting `on-surface` and
// silently relying on the page being light - `BrandLink`, which passes no
// colour of its own, and `Footer`'s links. Both were logged in 12.5 as latent
// and both come good here: the export's landing wordmark is white, and the
// footer now inherits rather than hardcoding. `Card` overrides this for its own
// contents, so the light card on the dark page stays readable.
export default function BareLayout({ children }: Props) {
  return (
    <div className="bg-gradient-start-page relative flex min-h-screen flex-col text-white">
      <BrandLink className="absolute left-6 top-6" />
      <div className="flex flex-1 flex-col">{children}</div>
      <Footer />
    </div>
  );
}
