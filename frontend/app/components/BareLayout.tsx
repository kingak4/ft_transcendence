import type { ReactNode } from 'react';

import BrandLink from './BrandLink';
import Footer from './Footer';

interface Props {
  children: ReactNode;
}

export default function BareLayout({ children }: Props) {
  return (
    <div className="bg-gradient-start-page text-on-start-page relative flex min-h-dvh flex-col">
      <header>
        <BrandLink className="absolute left-6 top-6" />
      </header>
      <main id="main" tabIndex={-1} className="flex flex-1 flex-col outline-none">
        {children}
      </main>
      <Footer tone="start-page" />
    </div>
  );
}
