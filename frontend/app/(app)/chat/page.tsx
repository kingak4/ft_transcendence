import Conversation from './Conversation';
import FriendRail from './FriendRail';
import { friends, messagesByFriendId } from './fixtures';

export const metadata = {
  title: 'Chat',
};

/**
 * Static chat screen. No REST, no STOMP - `fixtures.ts` is the seam where real
 * data arrives.
 *
 * Selection lives in `?friend=` rather than component state, which keeps this
 * whole subtree a Server Component: no client JS ships, and the selected
 * conversation survives a reload and can be linked to.
 *
 * In Next.js 15+ `searchParams` is a Promise and must be awaited - older
 * tutorials show it as a plain object.
 *
 * TODO(stomp): this is where the data source changes. Keep this component a
 * Server Component: fetch friends and message history here, and push the live
 * socket down into the smallest possible Client Component (the message list
 * and the composer). Marking this whole file `'use client'` would ship the
 * entire chat UI to the browser and lose the URL-driven selection below.
 *
 * TODO(stomp): `?friend=` currently identifies a *friend*. The send
 * destination is `/transcend/chat/{chatId}/send`, so a chat id must be
 * resolved from the friend - decide whether the URL should carry the chat id
 * instead, and whether an unknown `?friend=` should 404 rather than silently
 * falling back to the first friend as it does below.
 */
export default async function ChatPage({
  searchParams,
}: {
  searchParams: Promise<{ friend?: string | string[] }>;
}) {
  const { friend } = await searchParams;
  // A repeated query key (`?friend=a&friend=b`) arrives as an array.
  const friendId = Array.isArray(friend) ? friend[0] : friend;

  const activeFriend = friends.find((f) => f.id === friendId) ?? friends[0];

  if (!activeFriend) {
    return (
      <div className="bg-hub-panel text-hub-muted flex h-[calc(100vh-4rem)] items-center justify-center rounded-2xl text-sm">
        No conversations yet.
      </div>
    );
  }

  const messages = messagesByFriendId[activeFriend.id] ?? [];

  return (
    /*
     * The height must be *definite* for the message list to scroll: a
     * percentage or h-full resolves against the parent, and `(app)/layout.tsx`
     * sets `min-h-screen`, which does not make a height definite. So we compute
     * one from the viewport instead.
     *
     * 4rem = the `p-8` on <main> in app/(app)/layout.tsx, counted top and
     * bottom. If that padding changes, this number must change with it.
     *
     * TODO(design-migration): known bug - the page scrolls instead of the
     * message list, because <main> takes exactly 100vh and <Footer /> is
     * stacked below it. The fix is a footer-less `(chat)` route group with
     * `h-screen`, after which this calc() and the rounded-2xl inset both go
     * away and the panel sits edge-to-edge as designed. Also add
     * `overscroll-contain` to the message list in Conversation.tsx.
     */
    <div className="border-hub-border flex h-[calc(100vh-4rem)] overflow-hidden rounded-2xl border">
      <FriendRail friends={friends} activeFriendId={activeFriend.id} />
      <Conversation friend={activeFriend} messages={messages} />
    </div>
  );
}
