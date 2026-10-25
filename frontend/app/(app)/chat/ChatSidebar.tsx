'use client';

import { useMemo, useEffect, useId, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useTranslations } from 'next-intl';
import TextField from '../../components/TextField';
import UserRow from './UserRow';
import type { ChatUser } from './types';
import { usePresence } from '../../hooks/usePresence';
import { client } from '../../lib/api-clients';

interface Props {
  activeChats: { chatId: string; user: ChatUser }[];
  allUsers: ChatUser[];
  activeUserId: string;
  myUserId: string | null;
  hiddenOnNarrow?: boolean;
  searchPlaceholder?: string;
  emptyStateText?: string;
}

export default function ChatSidebar({
  activeChats,
  allUsers,
  activeUserId,
  myUserId,
  hiddenOnNarrow = false,
  searchPlaceholder,
  emptyStateText
}: Props) {
  const t = useTranslations('chat.rail');
  const tCommon = useTranslations('common');
  const unknownUser = tCommon('unknownUser');
  const shownPlaceholder = searchPlaceholder ?? t('searchPlaceholder');
  const titleId = useId();
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
            name: u.displayName ?? unknownUser,
            initial: (u.displayName ?? 'U').charAt(0).toUpperCase(),
            avatarId: u.avatarId?.val ?? null,
            online: false,
            status: 'Offline',
          }));
        setGlobalSearchResults(results);
      }
    }
    performSearch();
    return () => { active = false; };
  }, [debouncedQuery, myUserId, unknownUser]);

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
    <aside
      aria-labelledby={titleId}
      className={`bg-elevated-surface border-elevated-border ${hiddenOnNarrow ? 'hidden lg:flex' : 'flex'} w-full shrink-0 flex-col border-e lg:w-[290px]`}
    >
      {/* eslint-disable-next-line jsx-a11y/no-static-element-interactions -- delegation: tracks focus leaving the search field + results group */}
      <div
        className="relative flex flex-col gap-3 p-4"
        onBlur={(e) => {
          if (!e.currentTarget.contains(e.relatedTarget as Node)) {
            setIsFocused(false);
          }
        }}
      >
        <h2 id={titleId}className="text-on-elevated-surface text-base font-bold">{t('title')}</h2>
        <TextField
          tone="chat"
          size="sm"
          type="search"
          dir="auto"
          placeholder={shownPlaceholder}
          aria-label={shownPlaceholder}
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onFocus={() => setIsFocused(true)}
        />

        {(debouncedQuery || isFocused) && (
          <div className="absolute top-[100%] start-4 end-4 z-10 mt-1 max-h-64 overflow-y-auto rounded-xl border border-elevated-border bg-elevated-surface shadow-lg">
            {searchResults.length > 0 ? (
              <div className="flex flex-col p-2 gap-1">
                {searchResults.map((user) => (
                  <UserRow
                    key={user.id}
                    user={{ ...user, online: onlineStatus[user.id] ?? false }}
                    isActive={false}
                    onClick={() => setSearchInput('')}
                  />
                ))}
              </div>
            ) : (
              <div className="p-4 text-center text-sm text-muted">{emptyStateText ?? t('noUsersFound')}</div>
            )}
          </div>
        )}
      </div>

      {/*
       * TODO(stomp): `GET /friends` is paginated so this renders one page (size: 20),
       * not every friend. Needs infinite scroll on this container to load subsequent pages.
       */}
      <nav aria-labelledby={titleId}className="flex min-h-0 flex-1 flex-col gap-1 overflow-y-auto px-2 pb-4">
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
