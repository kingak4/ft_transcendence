'use client';

import { useMemo, useEffect, useState } from 'react';
import { useRouter, usePathname, useSearchParams } from 'next/navigation';
import TextField from '../../components/TextField';
import FriendRow from './FriendRow';
import type { Friend } from './types';
import { usePresence } from '../../hooks/usePresence';
import { client } from '../../lib/api-clients';
import { CHAT_DICT } from './dictionary';

interface Props {
  activeChats: { chatId: string; friend: Friend }[];
  allFriends: Friend[];
  activeFriendId: string;
  searchPlaceholder?: string;
  emptyStateText?: string;
}

export default function FriendRail({
  activeChats,
  allFriends,
  activeFriendId,
  searchPlaceholder = CHAT_DICT.rail.searchPlaceholder,
  emptyStateText = CHAT_DICT.rail.noUsersFound
}: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const currentQuery = searchParams.get('q') || '';
  const [searchInput, setSearchInput] = useState(currentQuery);
  const [isFocused, setIsFocused] = useState(false);
  const [globalSearchResults, setGlobalSearchResults] = useState<Friend[]>([]);

  const activeChatFriends = useMemo(() => activeChats.map((c) => c.friend), [activeChats]);

  useEffect(() => {
    let active = true;
    async function performSearch() {
      const { data, response } = await client.GET('/users/search', {
        params: { query: { query: currentQuery, size: 5 } }
      });
      if (active && response.ok && data?.content) {
        const results: Friend[] = data.content.map(u => ({
          id: u.id ?? '',
          name: u.displayName ?? 'Unknown User',
          initial: (u.displayName ?? 'U').charAt(0).toUpperCase(),
          color: 'bg-hub-panel',
          avatarId: u.avatarId?.val ?? null,
          online: false,
          status: 'Offline',
        }));
        setGlobalSearchResults(results);
      }
    }
    performSearch();
    return () => { active = false; };
  }, [currentQuery]);

  const searchResults = useMemo(() => {
    return globalSearchResults;
  }, [globalSearchResults]);

  const friendIds = useMemo(() => {
    const ids = new Set(activeChatFriends.map((f) => f.id));
    searchResults.forEach((f) => ids.add(f.id));
    return Array.from(ids);
  }, [activeChatFriends, searchResults]);

  const { isConnected, onlineStatus, checkPresence } = usePresence(friendIds);

  useEffect(() => {
    if (isConnected && friendIds.length > 0) {
      friendIds.forEach(id => checkPresence(id));
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
    <aside className="bg-hub-panel border-hub-border flex w-[290px] shrink-0 flex-col border-e">
      <div
        className="relative flex flex-col gap-3 p-4"
        onBlur={(e) => {
          if (!e.currentTarget.contains(e.relatedTarget as Node)) {
            setIsFocused(false);
          }
        }}
      >
        <h2 className="text-hub-on-surface text-base font-bold">{CHAT_DICT.rail.title}</h2>
        <TextField
          tone="chat"
          size="sm"
          type="search"
          placeholder={searchPlaceholder}
          aria-label={searchPlaceholder}
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onFocus={() => setIsFocused(true)}
        />

        {/* Search Results Dropdown */}
        {(currentQuery || isFocused) && (
          <div className="absolute top-[100%] left-4 right-4 z-10 mt-1 max-h-64 overflow-y-auto rounded-xl border border-hub-border bg-hub-panel shadow-lg">
            {searchResults.length > 0 ? (
              <div className="flex flex-col p-2 gap-1">
                {searchResults.map((friend) => (
                  <FriendRow
                    key={friend.id}
                    friend={{ ...friend, online: onlineStatus[friend.id] ?? false }}
                    isActive={false} // Never active in the search dropdown
                    onClick={() => setSearchInput('')}
                  />
                ))}
              </div>
            ) : (
              <div className="p-4 text-center text-sm text-hub-muted">{emptyStateText}</div>
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
