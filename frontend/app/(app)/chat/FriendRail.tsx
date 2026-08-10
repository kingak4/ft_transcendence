'use client';

import { useMemo, useEffect, useState } from 'react';
import { useRouter, usePathname, useSearchParams } from 'next/navigation';
import TextField from '../../components/TextField';
import FriendRow from './FriendRow';
import type { Friend } from './types';
import { usePresence } from '../../hooks/usePresence';

interface Props {
  activeChats: { chatId: string; friend: Friend }[];
  allFriends: Friend[];
  activeFriendId: string;
  /**
   * Unit 2b makes this rail one of two panes that take turns below `lg:`, and
   * only the route knows whose turn it is - it is the thing holding `?friend=`.
   *
   * The route passes the FACT, not the class. An earlier version took a
   * `className` string for the same reason and the reasoning behind it was
   * sound: `flex` and `hidden` are both `display`, so a component-level `flex`
   * and a caller-level `hidden` resolve by stylesheet order, not by the order
   * they are concatenated, and dropping the base `flex` genuinely avoided that.
   * The cost was that the rail could no longer render itself - `flex-col`,
   * `shrink-0` and `lg:w-[290px]` all assume a flex context it had stopped
   * establishing, so `""` or a forgotten prop produced a silently mis-stacked
   * rail and the route had to know this thing is built out of flexbox.
   *
   * A boolean keeps the cascade insight and takes the invariant back: exactly
   * one display class is ever emitted, so there is still nothing to arbitrate,
   * but the component is the one choosing it.
   */
  hiddenOnNarrow: boolean;
}

export default function FriendRail({
  activeChats,
  allFriends,
  activeFriendId,
  hiddenOnNarrow,
}: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const currentQuery = searchParams.get('q') || '';
  const [searchInput, setSearchInput] = useState(currentQuery);
  const [isFocused, setIsFocused] = useState(false);

  const activeChatFriends = useMemo(
    () => activeChats.map((c) => c.friend),
    [activeChats],
  );

  const searchResults = useMemo(() => {
    if (!currentQuery) {
      return isFocused ? allFriends.slice(0, 5) : [];
    }
    const lowerQuery = currentQuery.toLowerCase();
    return allFriends.filter((f) => f.name.toLowerCase().includes(lowerQuery));
  }, [allFriends, currentQuery, isFocused]);

  const friendIds = useMemo(() => {
    const ids = new Set(activeChatFriends.map((f) => f.id));
    searchResults.forEach((f) => ids.add(f.id));
    return Array.from(ids);
  }, [activeChatFriends, searchResults]);

  const { isConnected, onlineStatus, checkPresence } = usePresence(friendIds);

  useEffect(() => {
    if (isConnected && friendIds.length > 0) {
      friendIds.forEach((id) => checkPresence(id));
    }
  }, [isConnected, friendIds, checkPresence]);

  useEffect(() => {
    const timeout = setTimeout(() => {
      const params = new URLSearchParams(searchParams.toString());
      if (searchInput) {
        params.set('q', searchInput);
      } else {
        params.delete('q');
      }
      const newQueryString = params.toString();
      if (searchParams.toString() !== newQueryString) {
        router.replace(`${pathname}?${newQueryString}`, { scroll: false });
      }
    }, 300);
    return () => clearTimeout(timeout);
  }, [searchInput, router, pathname, searchParams]);

  return (
    // `border-e` is border-inline-end: the right edge in LTR, the left in RTL.
    // It only applies from `lg:` up, where there is a second pane for it to
    // divide from; below that this rail is the whole width and the edge would
    // draw against nothing.
    //
    // Width follows the same rule: full width when it is the only pane, the
    // export's 290px once both are on screen.
    <aside
      className={`bg-hub-panel border-hub-border w-full shrink-0 flex-col lg:w-[290px] lg:border-e ${
        hiddenOnNarrow ? 'hidden lg:flex' : 'flex'
      }`}
    >
      <div
        className="relative flex flex-col gap-3 p-4"
        onBlur={(e) => {
          if (!e.currentTarget.contains(e.relatedTarget as Node)) {
            setIsFocused(false);
          }
        }}
      >
        <h2 className="text-hub-on-surface text-base font-bold">Chats</h2>
        <TextField
          tone="chat"
          size="sm"
          type="search"
          placeholder="Search friends to create a conversation"
          aria-label="Search friends to create a conversation"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onFocus={() => setIsFocused(true)}
        />

        {/* Search Results Dropdown */}
        {(currentQuery || isFocused) && (
          <div className="border-hub-border bg-hub-panel absolute left-4 right-4 top-[100%] z-10 mt-1 max-h-64 overflow-y-auto rounded-xl border shadow-lg">
            {searchResults.length > 0 ? (
              <div className="flex flex-col gap-1 p-2">
                {searchResults.map((friend) => (
                  <FriendRow
                    key={friend.id}
                    friend={{
                      ...friend,
                      online: onlineStatus[friend.id] ?? false,
                    }}
                    isActive={false} // Never active in the search dropdown
                    onClick={() => setSearchInput('')}
                  />
                ))}
              </div>
            ) : (
              <div className="text-hub-muted p-4 text-center text-sm">
                No friends found
              </div>
            )}
          </div>
        )}
      </div>

      {/* min-h-0 lets this shrink below its content so overflow-y-auto can
       * actually engage; without it the list would push the rail taller.
       * TODO(stomp): `GET /friends` is paginated so this renders one page (size: 20),
       * not every friend. Needs infinite scroll on this container to load subsequent pages.
       */}
      <nav className="flex min-h-0 flex-1 flex-col gap-1 overflow-y-auto px-2 pb-4">
        {activeChatFriends.map((friend) => {
          return (
            <div key={friend.id}>
              <FriendRow
                friend={{ ...friend, online: onlineStatus[friend.id] ?? false }}
                isActive={friend.id === activeFriendId}
              />
            </div>
          );
        })}
      </nav>
    </aside>
  );
}
