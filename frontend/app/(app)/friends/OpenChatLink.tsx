import Link from 'next/link';

// The export ends each row with a single "Open chat" action. We keep Remove as
// well, so the row carries two - and this is the accent one.
//
// It takes the flat lime that AddFriendButton already uses rather than a third
// treatment invented for the occasion: both are "the primary action inside a
// list row", and a row where the accent action looks different depending on
// which list it is in would be harder to read, not richer.
//
// Geometry is the export's row button (9x18px, 10px radius, 13.5px at 700),
// which Remove now wears in full.
//
// The link names the friend: /chat?friend=<id>, which that route reads and
// resolves into the active conversation - the same URL its own FriendRow links
// to. Selection on /chat is URL state, not component state, so this is not a
// special case being added for us; it is the existing contract.
export default function OpenChatLink({ friendId }: { friendId: string }) {
  return (
    <Link
      href={`/chat?friend=${friendId}`}
      className="bg-primary text-on-primary px-4.5 py-2.25 rounded-[10px] text-sm font-bold transition-colors hover:brightness-125"
    >
      Chat
    </Link>
  );
}
