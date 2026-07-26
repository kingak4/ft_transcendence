import type { ReactNode } from 'react';

import BrandLink from './BrandLink';
import Footer from './Footer';

interface Props {
  children: ReactNode;
  brandLinkClassName?: string;
}

const DEFAULT_BRAND_LINK_CLASSES = 'text-on-surface absolute left-6 top-6';

// Shell for routes that shouldn't show the Sidebar (login, register, root) -
// just the brand mark, the page content, and the footer.
export default function BareLayout({ children, brandLinkClassName }: Props) {
  return (
    <div className="bg-surface text-on-surface relative flex min-h-screen flex-col">
      <BrandLink className={brandLinkClassName ?? DEFAULT_BRAND_LINK_CLASSES} />
      <div className="flex flex-1 flex-col">{children}</div>
      <Footer />
    </div>
  );
}
