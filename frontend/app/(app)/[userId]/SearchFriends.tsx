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

  // Kept in a ref (rather than the debounce effect's dependency array) so
  // the exclusion filter used mid-fetch always sees the latest friends/
  // added-ids without re-triggering a search every time they change.
  const exclusionRef = useRef({ currentUserId, existingFriendIds, addedIds });
  useEffect(() => {
    exclusionRef.current = { currentUserId, existingFriendIds, addedIds };
  });

  // Safe to call during render: reads props/state directly, not the ref.
  function isVisible(user: SearchUserResult): boolean {
    return (
      !!user.id &&
      user.id !== currentUserId &&
      !existingFriendIds.has(user.id) &&
      !addedIds.has(user.id)
    );
  }

  // For use only inside the async fetch loops below, where a plain closure
  // over props/state could go stale across an `await` (e.g. the user adds a
  // friend while a "Load more" fetch is in flight).
  function isVisibleLatest(user: SearchUserResult): boolean {
    const { currentUserId, existingFriendIds, addedIds } = exclusionRef.current;
    return (
      !!user.id &&
      user.id !== currentUserId &&
      !existingFriendIds.has(user.id) &&
      !addedIds.has(user.id)
    );
  }

  function handleQueryChange(e: React.ChangeEvent<HTMLInputElement>) {
    const value = e.target.value;
    setQuery(value);
    if (!value.trim()) {
      setResults([]);
      setError(null);
      setIsSearching(false);
      setPage(0);
      setHasMore(false);
    } else {
      setIsSearching(true);
    }
  }

  // Debounce: wait until the user pauses typing before hitting the backend,
  // and ignore any response that isn't from the most recent keystroke burst
  // (a slow earlier request could otherwise resolve after a newer one).
  useEffect(() => {
    const trimmed = query.trim();
    if (!trimmed) return;

    const requestId = ++latestRequestId.current;

    const handle = setTimeout(async () => {
      // Exclusion filtering (self, existing friends, just-added) happens
      // client-side, after pagination has already happened server-side. A
      // fetched page can therefore come back with zero *new* visible users
      // even though the server reports more pages exist. Keep fetching
      // subsequent pages until at least one is visible, or the server runs
      // out, so we never land on an empty list sitting above a "Load more"
      // button.
      let accumulated: SearchUserResult[] = [];
      let fetchedPage = 0;
      let more = false;

      while (true) {
        const result = await searchUsersAction(trimmed, fetchedPage, PAGE_SIZE);
        if (requestId !== latestRequestId.current) return;

        if (!result.success) {
          setIsSearching(false);
          setError(result.message);
          setResults([]);
          setHasMore(false);
          return;
        }

        accumulated = [...accumulated, ...result.results];
        more = result.hasMore;

        if (accumulated.some(isVisibleLatest) || !more) break;
        fetchedPage += 1;
      }

      setIsSearching(false);
      setError(null);
      setPage(fetchedPage);
      setResults(accumulated);
      setHasMore(more);
    }, SEARCH_DEBOUNCE_MS);

    return () => clearTimeout(handle);
  }, [query]);

  async function handleLoadMore() {
    const trimmed = query.trim();
    if (!trimmed) return;

    // Take a ticket from the same counter the debounce effect uses, so a
    // query change while this fetch loop is in flight (or a second
    // "Load more" click) invalidates this run's state updates below.
    const requestId = ++latestRequestId.current;
    setIsLoadingMore(true);

    let accumulated = results;
    let fetchedPage = page;
    let more = hasMore;

    try {
      while (more) {
        fetchedPage += 1;
        const result = await searchUsersAction(trimmed, fetchedPage, PAGE_SIZE);
        if (requestId !== latestRequestId.current) return;

        if (!result.success) {
          setError(result.message);
          return;
        }

        const visibleBefore = accumulated.filter(isVisibleLatest).length;
        accumulated = [...accumulated, ...result.results];
        more = result.hasMore;

        if (accumulated.filter(isVisibleLatest).length > visibleBefore) break;
      }

      if (requestId === latestRequestId.current) {
        setPage(fetchedPage);
        setResults(accumulated);
        setHasMore(more);
      }
    } finally {
      setIsLoadingMore(false);
    }
  }

  const visibleResults = results.filter(isVisible);

  return (
    <div className="mb-4 flex flex-col gap-2">
      <input
        type="text"
        value={query}
        onChange={handleQueryChange}
        placeholder="Search users by name…"
        className="text-on-surface placeholder:text-on-surface/40 focus:ring-primary w-full rounded-lg bg-on-surface/10 px-3 py-2 text-sm outline-none focus:ring-1"
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
              className="text-on-surface/60 bg-on-surface/10 hover:bg-on-surface/20 self-center rounded-lg px-3 py-1.5 text-xs font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isLoadingMore ? 'Loading…' : 'Load more'}
            </button>
          )}
        </>
      )}
    </div>
  );
}
