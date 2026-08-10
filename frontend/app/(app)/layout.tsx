import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { assertValidSession } from '../lib/session';

import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import ConnectionBanner from '../components/ConnectionBanner';
import StompProvider from '../components/StompProvider';

export default async function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const userId = cookieStore.get('user_id')?.value ?? null;

  if (!userId) {
    redirect('/login');
  }

  const isValidSession = await assertValidSession();

  if (!isValidSession) {
    redirect('/auth/logout');
  }

  return (
    <StompProvider>
      <div className="bg-surface text-on-surface flex h-full flex-col min-h-0">
        <ConnectionBanner />
        {/* `flex-col` on the parent is load-bearing, not decoration. It was lost
          in a rebase and the default `row` turned ConnectionBanner - an in-flow
          `shrink-0` block - into a full-height column pinned to the left edge,
          shoving the rail and the whole page sideways. Every child's sizing rule
          changes axis with the container, and the banner's `border-b` is the
          tell: a bottom border only means anything if the thing below it is
          below it. Only visible while the socket is down, which is why it
          survived review. */}
        <div className="flex flex-1 min-h-0">
          <Sidebar userId={userId} />
          <div className="flex flex-1 flex-col min-h-0">
            {/* Page padding for app routes, from the dictionary (12.0): 48/56px
              on wide screens, 24/16px narrow - two 56px gutters take a third of
              a 360px viewport. Was a flat p-8 (32px) at both widths.

              The narrow top padding is 80px rather than 24px because unit 1b
              puts a fixed hamburger button in that corner. It is reserved space,
              not a spacing decision, which is why it disappears at `lg:` where
              the rail is a column again and nothing overlaps the content. */}
            {/* `flex flex-col` so a route can ask for "the rest of the height"
              with `flex-1` instead of computing it. Every page under here
              already opens with `flex flex-1 flex-col`, which did nothing while
              this was a block box - /chat was the only route that needed the
              height and it got it from `calc(100vh-4rem)`, a number that was
              true only while this element had `p-8`. Position 11 changed the
              padding and quietly made it false.

              `overflow-y-auto` is the other half of the `min-h-0` chain above,
              and the two only make sense together. `min-h-0` revokes a flex
              item's refusal to shrink below its content, which is what gives
              /chat's message pane a bounded height to scroll inside. But a
              bound without an escape hatch is just clipping: every route that
              is TALLER than the viewport - the legal pages, most obviously -
              had its content spill out of this box with nothing to catch it,
              because the shell is pinned to `h-full` and the document cannot
              grow. This makes `main` the one scroll container. /chat is
              unaffected: its content is already bounded to this height, so
              this never scrolls there and its inner pane still owns the
              scrolling. */}
            <main className="flex flex-1 flex-col min-h-0 overflow-y-auto px-4 pb-6 pt-20 lg:px-14 lg:pb-12 lg:pt-12">
              {children}
            </main>
            <Footer />
          </div>
        </div>
      </div>
    </StompProvider>
  );
}
