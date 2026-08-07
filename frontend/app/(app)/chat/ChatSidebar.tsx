'use client';

import { useMemo, useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import TextField from '../../components/TextField';
import UserRow from './UserRow';
import type { ChatUser } from './types';
import { usePresence } from '../../hooks/usePresence';
import { client } from '../../lib/api-clients';
import { CHAT_DICT } from './dictionary';

interface Props {
  activeChats: { chatId: string; user: ChatUser }[];
  allUsers: ChatUser[];
  activeUserId: string;
  myUserId: string | null;
  searchPlaceholder?: string;
  emptyStateText?: string;
}

export default function ChatSidebar({
  activeChats,
  allUsers,
  activeUserId,
  myUserId,
  searchPlaceholder = CHAT_DICT.rail.searchPlaceholder,
  emptyStateText = CHAT_DICT.rail.noUsersFound
}: Props) {
  const searchParams = useSearchParams();
  const initialQuery = searchParams.get('q') || '';
  const [searchInput, setSearchInput] = useState(initialQuery);
  const [debouncedQuery, setDebouncedQuery] = useState(initialQuery);
  const [isFocused, setIsFocused] = useState(false);
  const [globalSearchResults, setGlobalSearchResults] = useState<ChatUser[]>([]);

  const sidebarUsers = useMemo(() => {
    const userMap = new Map<string, ChatUser>();
    activeChats.forEach((c) => userMap.set(c.user.id, c.user));
    allUsers.forEach((u) => userMap.set(u.id, u));
    return Array.from(userMap.values());
  }, [activeChats, allUsers]);

  useEffect(() => {
    let active = true;
    async function performSearch() {
      const { data, response } = await client.GET('/users/search', {
        params: { query: { query: debouncedQuery, size: 5 } }
      });
      if (active && response.ok && data?.content) {
        const results: ChatUser[] = data.content
          .filter(u => u.id !== myUserId)
          .map(u => ({
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
  }, [debouncedQuery]);

  const searchResults = useMemo(() => {
    return globalSearchResults;
  }, [globalSearchResults]);

  const userIds = useMemo(() => {
    const ids = new Set(sidebarUsers.map((u) => u.id));
    searchResults.forEach((u) => ids.add(u.id));
    return Array.from(ids);
  }, [sidebarUsers, searchResults]);

  const { isConnected, onlineStatus, checkPresence } = usePresence(userIds);

  useEffect(() => {
    if (isConnected && userIds.length > 0) {
      userIds.forEach(id => checkPresence(id));
    }
  }, [isConnected, userIds, checkPresence]);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedQuery(searchInput);
    }, 300);
    return () => clearTimeout(timeout);
  }, [searchInput]);

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
        {(debouncedQuery || isFocused) && (
          <div className="absolute top-[100%] left-4 right-4 z-10 mt-1 max-h-64 overflow-y-auto rounded-xl border border-hub-border bg-hub-panel shadow-lg">
            {searchResults.length > 0 ? (
              <div className="flex flex-col p-2 gap-1">
                {searchResults.map((user) => (
                  <UserRow
                    key={user.id}
                    user={{ ...user, online: onlineStatus[user.id] ?? false }}
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
        {sidebarUsers.map((user) => {
          return (
            <div key={user.id}>
              <UserRow
                user={{ ...user, online: onlineStatus[user.id] ?? false }}
                isActive={user.id === activeUserId}
              />
            </div>
          );
        })}
      </nav>
    </aside>
  );
}
