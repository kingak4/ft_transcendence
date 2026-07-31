import TextField from '../../components/TextField';
import FriendRow from './FriendRow';
import type { Friend } from './fixtures';

interface Props {
  friends: Friend[];
  activeFriendId: string;
}

export default function FriendRail({ friends, activeFriendId }: Props) {
  return (
    // `border-e` is border-inline-end: the right edge in LTR, the left in RTL.
    <aside className="bg-hub-panel border-hub-border flex w-[290px] shrink-0 flex-col border-e">
      <div className="flex flex-col gap-3 p-4">
        <h2 className="text-hub-on-surface text-base font-bold">Chats</h2>
        {/* Inert on this branch - filtering needs client state, which would
            cost the page its zero-JS Server Component status for no benefit
            while the data is fixtures.
            TODO(design-migration): make this filter. Prefer a `?q=` search param over
            useState so the rail stays a Server Component and the URL keeps
            describing the view, matching how `?friend=` already works. Reach
            for a Client Component only if you need debounced typing. */}
        <TextField
          tone="chat"
          size="sm"
          type="search"
          placeholder="Search friends"
          aria-label="Search friends"
        />
      </div>

      {/* min-h-0 lets this shrink below its content so overflow-y-auto can
          actually engage; without it the list would push the rail taller.
          TODO(stomp): `GET /friends` is paginated (see commit "ref: paginate
          GET /friends endpoint"), so this renders one page, not every friend.
          Needs either infinite scroll on this container or explicit paging -
          silently showing page 1 as if it were the whole list is the bug to
          avoid. Add an empty state for a user with no friends too. */}
      <nav className="flex min-h-0 flex-1 flex-col gap-1 overflow-y-auto px-2 pb-4">
        {friends.map((friend) => (
          <FriendRow
            key={friend.id}
            friend={friend}
            isActive={friend.id === activeFriendId}
          />
        ))}
      </nav>
    </aside>
  );
}
