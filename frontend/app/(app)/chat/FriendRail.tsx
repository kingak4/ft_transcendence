'use client';

import { useMemo, useEffect, useState, useCallback, useRef } from 'react';
import { useRouter, usePathname, useSearchParams } from 'next/navigation';
import TextField from '../../components/TextField';
import FriendRow from './FriendRow';
import type { Friend } from './types';
import { usePresence } from '../../hooks/usePresence';
import { client } from '../../lib/api-clients';

interface Props {
  activeChats: { chatId: string; friend: Friend }[];
  allFriends: Friend[];
  activeFriendId: string;
}

export default function FriendRail({ activeChats, allFriends, activeFriendId }: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const currentQuery = searchParams.get('q') || '';
  const [searchInput, setSearchInput] = useState(currentQuery);

  const activeChatFriends = useMemo(() => activeChats.map((c) => c.friend), [activeChats]);

  const searchResults = useMemo(() => {
    if (!currentQuery) return [];
    const lowerQuery = currentQuery.toLowerCase();
    return allFriends.filter((f) => f.name.toLowerCase().includes(lowerQuery));
  }, [allFriends, currentQuery]);

  const friendIds = useMemo(() => activeChatFriends.map((f) => f.id), [activeChatFriends]);
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
      <div className="relative flex flex-col gap-3 p-4">
        <h2 className="text-hub-on-surface text-base font-bold">Chats</h2>
        <TextField
          tone="chat"
          size="sm"
          type="search"
          placeholder="Search friends to create a conversation"
          aria-label="Search friends to create a conversation"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />

        {/* Search Results Dropdown */}
        {currentQuery && (
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
              <div className="p-4 text-center text-sm text-hub-muted">No friends found</div>
            )}
          </div>
        )}
      </div>

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
