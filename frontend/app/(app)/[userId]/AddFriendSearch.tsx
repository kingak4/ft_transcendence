'use client';

import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';

import AddFriendButton from './AddFriendButton';
import { searchUsersAction, type SearchUserResult } from './actions';

interface Props {
  currentUserId: string;
  existingFriendIds: Set<string>;
}

const SEARCH_DEBOUNCE_MS = 300;

export default function AddFriendSearch({
  currentUserId,
  existingFriendIds,
}: Props) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchUserResult[]>([]);
  const [addedIds, setAddedIds] = useState<Set<string>>(new Set());
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState<string | null>(null);
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
      return;
    }

    const requestId = ++latestRequestId.current;
    setIsSearching(true);

    const handle = setTimeout(async () => {
      const result = await searchUsersAction(trimmed);
      if (requestId !== latestRequestId.current) return;

      setIsSearching(false);
      if (!result.success) {
        setError(result.message);
        setResults([]);
        return;
      }
      setError(null);
      setResults(result.results);
    }, SEARCH_DEBOUNCE_MS);

    return () => clearTimeout(handle);
  }, [query]);

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

      {query.trim() &&
        !isSearching &&
        !error &&
        visibleResults.length === 0 && (
          <p className="text-brand-reversed-main-color/40 text-sm">
            No users found.
          </p>
        )}

      {visibleResults.length > 0 && (
        <ul className="flex flex-col gap-2">
          {visibleResults.map((user) => {
            const avatarSrc = user.avatarId
              ? `/api/users/avatar/${user.avatarId}`
              : null;

            return (
              <li
                key={user.id}
                className="bg-brand-reversed-main-color flex items-center justify-between gap-3 rounded-xl p-3"
              >
                <div className="flex items-center gap-3">
                  {avatarSrc ? (
                    <Image
                      src={avatarSrc}
                      alt={`${user.displayName}'s avatar`}
                      width={40}
                      height={40}
                      className="h-10 w-10 rounded-full object-cover"
                    />
                  ) : (
                    <div className="h-10 w-10 rounded-full bg-blue-200" />
                  )}
                  <p className="text-brand-main-color font-medium">
                    {user.displayName}
                  </p>
                </div>
                <AddFriendButton
                  friendId={user.id}
                  onAdded={() =>
                    setAddedIds((prev) => new Set(prev).add(user.id))
                  }
                />
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
