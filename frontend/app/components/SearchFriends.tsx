'use client';

import { useEffect, useRef, useState, type ReactNode } from 'react';

import UserList, { type UserListItem } from './UserList';

// The contract a caller's search action must satisfy. Declared here rather
// than imported from a route's actions file so this component stays free of
// any route dependency: TypeScript is structurally typed, so any action whose
// return shape matches can be passed in.
export type UserSearchPage =
  | { success: true; results: UserListItem[]; hasMore: boolean }
  | { success: false; message: string };

interface Props {
  currentUserId: string;
  excludedIds: Set<string>;
  searchAction: (
    query: string,
    page: number,
    size: number,
  ) => Promise<UserSearchPage>;
  // `dismiss` hides the row once the caller's action succeeds; the caller
  // decides what that action is (add a friend, invite to a game, …).
  renderAction: (user: UserListItem, dismiss: () => void) => ReactNode;
  placeholder?: string;
  emptyMessage?: string;
}

const SEARCH_DEBOUNCE_MS = 300;
const PAGE_SIZE = 10;

export default function SearchFriends({
  currentUserId,
  excludedIds,
  searchAction,
  renderAction,
  placeholder = 'Search users by name…',
  emptyMessage = 'No users found.',
}: Props) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<UserListItem[]>([]);
  const [dismissedIds, setDismissedIds] = useState<Set<string>>(new Set());
  const [isSearching, setIsSearching] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const latestRequestId = useRef(0);

  // Kept in a ref (rather than the debounce effect's dependency array) so the
  // exclusion filter and search action used mid-fetch always see the latest
  // props/state without re-triggering a search every time they change — which
  // matters especially for `searchAction`, since a caller passing an inline
  // arrow would otherwise change its identity on every render.
  const latestRef = useRef({
    currentUserId,
    excludedIds,
    dismissedIds,
    searchAction,
  });
  useEffect(() => {
    latestRef.current = {
      currentUserId,
      excludedIds,
      dismissedIds,
      searchAction,
    };
  });

  // Safe to call during render: reads props/state directly, not the ref.
  function isVisible(user: UserListItem): boolean {
    return (
      !!user.id &&
      user.id !== currentUserId &&
      !excludedIds.has(user.id) &&
      !dismissedIds.has(user.id)
    );
  }

  // For use only inside the async fetch loops below, where a plain closure
  // over props/state could go stale across an `await` (e.g. the user adds a
  // friend while a "Load more" fetch is in flight).
  function isVisibleLatest(user: UserListItem): boolean {
    const { currentUserId, excludedIds, dismissedIds } = latestRef.current;
    return (
      !!user.id &&
      user.id !== currentUserId &&
      !excludedIds.has(user.id) &&
      !dismissedIds.has(user.id)
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
      // Exclusion filtering (self, excluded ids, just-dismissed) happens
      // client-side, after pagination has already happened server-side. A
      // fetched page can therefore come back with zero *new* visible users
      // even though the server reports more pages exist. Keep fetching
      // subsequent pages until at least one is visible, or the server runs
      // out, so we never land on an empty list sitting above a "Load more"
      // button.
      let accumulated: UserListItem[] = [];
      let fetchedPage = 0;
      let more = false;

      while (true) {
        const result = await latestRef.current.searchAction(
          trimmed,
          fetchedPage,
          PAGE_SIZE,
        );
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
        const result = await latestRef.current.searchAction(
          trimmed,
          fetchedPage,
          PAGE_SIZE,
        );
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
        placeholder={placeholder}
        className="text-on-surface placeholder:text-on-surface/40 focus:ring-primary bg-on-surface/10 w-full rounded-lg px-3 py-2 text-sm outline-none focus:ring-1"
      />

      {error && <p className="text-xs text-red-400">{error}</p>}

      {query.trim() && !isSearching && !error && (
        <>
          <UserList
            users={visibleResults}
            emptyMessage={emptyMessage}
            renderAction={(user) =>
              renderAction(user, () =>
                setDismissedIds((prev) => new Set(prev).add(user.id)),
              )
            }
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
