'use client';

import { useState, useEffect } from 'react';
import { useChat } from '../../../hooks/useChat';
import { client } from '../../../lib/api-clients';
import Button from '../../../components/Button';
import TextField from '../../../components/TextField';
import Avatar from '../../../components/Avatar';
import { ChatUser } from '../../chat/types';

interface Props {
  gameName: string;
  isOpen: boolean;
  onClose: () => void;
  myUserId: string | null;
  currentScore?: number;
}

export default function InviteModal({
  gameName,
  isOpen,
  onClose,
  myUserId,
  currentScore,
}: Props) {
  const [searchInput, setSearchInput] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [searchResults, setSearchResults] = useState<ChatUser[]>([]);
  const [isSending, setIsSending] = useState<string | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [includeScore, setIncludeScore] = useState(true);

  const { sendMessage, isConnected } = useChat();

  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedQuery(searchInput);
    }, 300);
    return () => clearTimeout(timeout);
  }, [searchInput]);

  useEffect(() => {
    let active = true;
    async function performSearch() {
      if (!debouncedQuery.trim()) {
        setSearchResults([]);
        return;
      }
      try {
        const { data, response } = await client.GET('/users/search', {
          params: { query: { query: debouncedQuery, size: 5 } },
        });
        if (active && response.ok && data?.content) {
          const results: ChatUser[] = data.content
            .filter((u) => u.id !== myUserId)
            .map((u) => ({
              id: u.id ?? '',
              name: u.displayName ?? 'Unknown User',
              initial: (u.displayName ?? 'U').charAt(0).toUpperCase(),
              color: 'bg-elevated-surface',
              avatarId: u.avatarId?.val ?? null,
              online: false,
              status: 'Offline',
            }));
          setSearchResults(results);
        }
      } catch (err) {
        console.error('Search failed', err);
      }
    }
    performSearch();
    return () => {
      active = false;
    };
  }, [debouncedQuery, myUserId]);

  const handleClose = () => {
    setErrorMsg(null);
    setSuccessMsg(null);
    setSearchInput('');
    onClose();
  };

  const handleInvite = async (user: ChatUser) => {
    if (!isConnected) {
      setErrorMsg('No connection to chat service (STOMP)');
      return;
    }
    setErrorMsg(null);
    setSuccessMsg(null);
    setIsSending(user.id);

    try {
      const { data, response } = await client.POST('/chats/{recipientId}', {
        params: { path: { recipientId: user.id } },
      });

      if (!response.ok || !data) {
        throw new Error('Failed to create/get chat');
      }

      const chatId = data.chatId;
      const payload =
        includeScore && currentScore !== undefined && currentScore > 0
          ? `#game-invite:${gameName}:${currentScore}#`
          : `#game-invite:${gameName}#`;

      const success = sendMessage(chatId as string, payload);

      if (success) {
        setSuccessMsg(`Challenge sent to ${user.name}!`);
        setSearchInput('');
      } else {
        setErrorMsg('Failed to send challenge over socket.');
      }
    } catch (err) {
      console.error(err);
      setErrorMsg('Failed to send challenge.');
    } finally {
      setIsSending(null);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="animate-in fade-in fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm duration-200">
      <div className="bg-elevated-surface border-elevated-border w-full max-w-md rounded-2xl border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-on-elevated-surface text-xl font-bold">
            {currentScore !== undefined && currentScore > 0
              ? 'Share & Challenge'
              : 'Challenge a Friend'}
          </h2>
          <button
            onClick={handleClose}
            className="text-muted hover:text-on-elevated-surface cursor-pointer rounded-lg p-1 transition-colors"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <p className="text-muted mb-4 text-sm">
          Search for a friend to challenge them in{' '}
          <span className="text-on-elevated-surface font-bold">{gameName}</span>
          .
        </p>

        {currentScore !== undefined && currentScore > 0 && (
          <div className="bg-sunken-surface border-elevated-border mb-4 flex items-center justify-between rounded-xl border p-3">
            <div className="flex flex-col">
              <span className="text-on-elevated-surface text-xs font-bold">
                Brag with current score
              </span>
              <span className="text-muted text-[11px]">
                Include score to beat:{' '}
                <strong className="text-accent-teal">{currentScore} pts</strong>
              </span>
            </div>
            <label className="relative inline-flex cursor-pointer items-center">
              <input
                type="checkbox"
                checked={includeScore}
                onChange={(e) => setIncludeScore(e.target.checked)}
                className="peer sr-only"
              />
              <div className="bg-field peer-checked:bg-accent-teal peer h-5 w-10 rounded-full after:absolute after:left-[2px] after:top-[2px] after:h-4 after:w-4 after:rounded-full after:border after:border-gray-300 after:bg-white after:transition-all after:content-[''] peer-checked:after:translate-x-full peer-checked:after:border-white peer-focus:outline-none"></div>
            </label>
          </div>
        )}

        <TextField
          id="friend-search"
          type="search"
          placeholder="Search players..."
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          className="mb-4"
          tone="chat"
        />

        {errorMsg && (
          <div className="border-danger/20 bg-danger/10 text-danger mb-4 rounded-xl border p-2.5 text-xs font-medium">
            {errorMsg}
          </div>
        )}
        {successMsg && (
          <div className="border-success/20 bg-success/10 text-success mb-4 rounded-xl border p-2.5 text-xs font-medium">
            {successMsg}
          </div>
        )}

        <div className="flex max-h-60 flex-col gap-2 overflow-y-auto">
          {searchResults.length > 0 ? (
            searchResults.map((u) => (
              <div
                key={u.id}
                className="border-elevated-border bg-field/50 hover:bg-field flex items-center justify-between rounded-xl border p-3 transition-colors"
              >
                <div className="flex items-center gap-3">
                  <Avatar
                    src={u.avatarId ? `/api/avatars/${u.avatarId}` : null}
                    alt={u.name}
                    initial={u.initial}
                    size={36}
                  />
                  <span className="text-on-elevated-surface text-sm font-semibold">
                    {u.name}
                  </span>
                </div>
                <Button
                  variant="primary"
                  onClick={() => handleInvite(u)}
                  disabled={isSending === u.id}
                  className="px-3.5 py-1.5 text-xs"
                >
                  {isSending === u.id ? 'Sending...' : 'Challenge'}
                </Button>
              </div>
            ))
          ) : debouncedQuery ? (
            <div className="text-muted p-4 text-center text-sm">
              No players found.
            </div>
          ) : (
            <div className="text-muted p-4 text-center text-sm opacity-60">
              Type a player name above to search.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
