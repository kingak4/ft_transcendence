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
}

export default function InviteModal({ gameName, isOpen, onClose, myUserId }: Props) {
  const [searchInput, setSearchInput] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [searchResults, setSearchResults] = useState<ChatUser[]>([]);
  const [isSending, setIsSending] = useState<string | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const { sendMessage, isConnected } = useChat();

  // Debounce search
  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedQuery(searchInput);
    }, 300);
    return () => clearTimeout(timeout);
  }, [searchInput]);

  // Perform search
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
              color: 'bg-hub-panel',
              avatarId: u.avatarId?.val ?? null,
              online: false, // We don't fetch presence here for simplicity
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

  const handleInvite = async (user: ChatUser) => {
    if (!isConnected) {
      setErrorMsg('Brak połączenia z czatem (STOMP)');
      return;
    }
    setErrorMsg(null);
    setSuccessMsg(null);
    setIsSending(user.id);

    try {
      // 1. Get or create chat
      const { data, response } = await client.POST('/chats/{recipientId}', {
        params: { path: { recipientId: user.id } },
      });

      if (!response.ok || !data) {
        throw new Error('Failed to create/get chat');
      }

      const chatId = data.chatId;

      // 2. Send the invite message
      const success = sendMessage(chatId as string, `#game-invite:${gameName}#`);
      
      if (success) {
        setSuccessMsg(`Wysłano zaproszenie do ${user.name}!`);
        // Clear search to prepare for next
        setSearchInput('');
      } else {
        setErrorMsg('Błąd podczas wysyłania przez socket.');
      }
    } catch (err) {
      console.error(err);
      setErrorMsg('Błąd przy zapraszaniu gracza.');
    } finally {
      setIsSending(null);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
      <div className="w-full max-w-md rounded-xl bg-hub-panel p-6 shadow-xl border border-hub-border">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-bold text-hub-on-surface">Zaproś do gry</h2>
          <button
            onClick={onClose}
            className="text-hub-muted hover:text-hub-on-surface"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <p className="mb-4 text-sm text-hub-muted">
          Wyszukaj znajomego, któremu chcesz wysłać wyzwanie w grze {gameName}.
        </p>

        <TextField
          id="friend-search"
          type="search"
          placeholder="Szukaj graczy..."
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          className="mb-4"
          tone="chat"
        />

        {errorMsg && (
          <div className="mb-4 rounded-md bg-red-500/10 p-2 text-sm text-red-500">
            {errorMsg}
          </div>
        )}
        {successMsg && (
          <div className="mb-4 rounded-md bg-green-500/10 p-2 text-sm text-green-500">
            {successMsg}
          </div>
        )}

        <div className="flex flex-col gap-2 max-h-60 overflow-y-auto">
          {searchResults.length > 0 ? (
            searchResults.map((u) => (
              <div
                key={u.id}
                className="flex items-center justify-between rounded-lg border border-hub-border bg-black/5 dark:bg-white/5 p-3"
              >
                <div className="flex items-center gap-3">
                  <Avatar user={u} size="sm" />
                  <span className="font-semibold text-hub-on-surface">{u.name}</span>
                </div>
                <Button
                  size="sm"
                  variant="primary"
                  onClick={() => handleInvite(u)}
                  disabled={isSending === u.id}
                >
                  {isSending === u.id ? 'Wysyłanie...' : 'Zaproś'}
                </Button>
              </div>
            ))
          ) : debouncedQuery ? (
            <div className="p-4 text-center text-sm text-hub-muted">
              Nie znaleziono graczy.
            </div>
          ) : (
            <div className="p-4 text-center text-sm text-hub-muted opacity-60">
              Wpisz nazwę gracza powyżej.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
