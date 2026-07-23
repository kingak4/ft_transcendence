'use client';

import { useEffect, useRef, useState } from 'react';

import AddFriendButton from './AddFriendButton';
import UserList from './UserList';
import { searchUsersAction, type SearchUserResult } from './actions';

interface Props {
  currentUserId: string;
  existingFriendIds: Set<string>;
}

const SEARCH_DEBOUNCE_MS = 300;
const PAGE_SIZE = 10;

export default function SearchFriends({
  currentUserId,
  existingFriendIds,
}: Props) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchUserResult[]>([]);
  const [addedIds, setAddedIds] = useState<Set<string>>(new Set());
  const [isSearching, setIsSearching] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const latestRequestId = useRef(0);

  // Debounce: wait until the user pauses typing before hitting the backend,
  // and ignore any response that isn't from the most recent keystroke burst
  // (a slow earlier request could otherwise resolve after a newer one).
  useEffect(() => {
    const trimmed = query.trim();
    if (!trimmed) {
      setResults([]);
      setError(null);
      setIsSearching(false);
      setPage(0);
      setHasMore(false);
      return;
    }

    const requestId = ++latestRequestId.current;
    setIsSearching(true);

    const handle = setTimeout(async () => {
      const result = await searchUsersAction(trimmed, 0, PAGE_SIZE);
      if (requestId !== latestRequestId.current) return;

      setIsSearching(false);
      setPage(0);
      if (!result.success) {
        setError(result.message);
        setResults([]);
        setHasMore(false);
        return;
      }
      setError(null);
      setResults(result.results);
      setHasMore(result.hasMore);
    }, SEARCH_DEBOUNCE_MS);

    return () => clearTimeout(handle);
  }, [query]);

  async function handleLoadMore() {
    const trimmed = query.trim();
    if (!trimmed) return;

    const nextPage = page + 1;
    setIsLoadingMore(true);

    const result = await searchUsersAction(trimmed, nextPage, PAGE_SIZE);

    setIsLoadingMore(false);
    if (!result.success) {
      setError(result.message);
      return;
    }
    setPage(nextPage);
    setResults((prev) => [...prev, ...result.results]);
    setHasMore(result.hasMore);
  }

  const visibleResults = results.filter(
    (user) =>
      user.id &&
      user.id !== currentUserId &&
      !existingFriendIds.has(user.id) &&
      !addedIds.has(user.id),
  );

  return (
    <div className="mb-4 flex flex-col gap-2">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search users by name…"
        className="text-brand-reversed-main-color placeholder:text-brand-reversed-main-color/40 focus:ring-brand-secondary-color w-full rounded-lg bg-white/10 px-3 py-2 text-sm outline-none focus:ring-1"
      />

      {error && <p className="text-xs text-red-400">{error}</p>}

      {query.trim() && !isSearching && !error && (
        <>
          <UserList
            users={visibleResults}
            emptyMessage="No users found."
            renderAction={(user) => (
              <AddFriendButton
                friendId={user.id}
                onAdded={() =>
                  setAddedIds((prev) => new Set(prev).add(user.id))
                }
              />
            )}
          />
          {hasMore && (
            <button
              onClick={handleLoadMore}
              disabled={isLoadingMore}
              className="text-brand-reversed-main-color/60 self-center rounded-lg bg-white/10 px-3 py-1.5 text-xs font-medium transition-colors hover:bg-white/20 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isLoadingMore ? 'Loading…' : 'Load more'}
            </button>
          )}
        </>
      )}
    </div>
  );
}
