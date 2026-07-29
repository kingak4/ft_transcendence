import type { ReactNode } from 'react';

import BrandLink from './BrandLink';
import Footer from './Footer';

interface Props {
  children: ReactNode;
}

// Shell for routes that shouldn't show the Sidebar (login, register, root) -
// just the brand mark, the page content, and the footer.
export default function BareLayout({ children }: Props) {
  return (
    <div className="bg-surface text-on-surface relative flex min-h-screen flex-col">
      <BrandLink className="absolute left-6 top-6" />
      <div className="flex flex-1 flex-col">{children}</div>
      <Footer />
    </div>
  );
}
