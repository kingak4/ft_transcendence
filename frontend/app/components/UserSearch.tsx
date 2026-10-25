'use client';

import { useTranslations } from 'next-intl';
import { useEffect, useRef, useState, type ReactNode } from 'react';

import TextField from './TextField';
import UserList, { type UserListItem } from './UserList';

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
  renderAction: (user: UserListItem, dismiss: () => void) => ReactNode;
  label: string;
  placeholder?: string;
  emptyMessage?: string;
}

const SEARCH_DEBOUNCE_MS = 300;
const PAGE_SIZE = 10;

export default function UserSearch({
  currentUserId,
  excludedIds,
  searchAction,
  renderAction,
  label,
  placeholder,
  emptyMessage,
}: Props) {
  const t = useTranslations('userSearch');
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<UserListItem[]>([]);
  const [dismissedIds, setDismissedIds] = useState<Set<string>>(new Set());
  const [isSearching, setIsSearching] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const latestRequestId = useRef(0);

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

  function isVisible(user: UserListItem): boolean {
    return (
      !!user.id &&
      user.id !== currentUserId &&
      !excludedIds.has(user.id) &&
      !dismissedIds.has(user.id)
    );
  }

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

  useEffect(() => {
    const trimmed = query.trim();
    if (!trimmed) return;

    const requestId = ++latestRequestId.current;

    const handle = setTimeout(async () => {
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
    <div className="mb-4 flex flex-col gap-5 lg:gap-7">
      <TextField
        type="search"
        aria-label={label}
        value={query}
        onChange={handleQueryChange}
        placeholder={placeholder ?? t('placeholder')}
        dir="auto"
        size="md"
      />

      {error && (
        <p role="alert" className="text-danger text-xs">
          {error}
        </p>
      )}

      {query.trim() && !isSearching && !error && (
        <>
          <UserList
            users={visibleResults}
            emptyMessage={emptyMessage ?? t('empty')}
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
              className="text-on-surface bg-on-surface/10 hover:bg-on-surface/20 self-center rounded-[10px] px-3 py-1.5 text-xs font-semibold transition-colors disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isLoadingMore ? t('loading') : t('loadMore')}
            </button>
          )}
        </>
      )}
    </div>
  );
}
