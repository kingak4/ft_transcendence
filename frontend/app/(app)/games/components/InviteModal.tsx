'use client';

import { useTranslations } from 'next-intl';
import { useState, useEffect } from 'react';
import { useChat } from '../../../hooks/useChat';
import { client } from '../../../lib/api-clients';
import Button from '../../../components/Button';
import TextField from '../../../components/TextField';
import Avatar from '../../../components/Avatar';
import Dialog from '../../../components/Dialog';
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
  const t = useTranslations('games.invite');
  const tCommon = useTranslations('common');
  const unknownUser = tCommon('unknownUser');
  const offline = tCommon('offline');
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
              name: u.displayName ?? unknownUser,
              initial: (u.displayName ?? 'U').charAt(0).toUpperCase(),
              color: 'bg-elevated-surface',
              avatarId: u.avatarId?.val ?? null,
              online: false,
              status: offline,
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
  }, [debouncedQuery, myUserId, unknownUser, offline]);

  const handleClose = () => {
    setErrorMsg(null);
    setSuccessMsg(null);
    setSearchInput('');
    onClose();
  };

  const handleInvite = async (user: ChatUser) => {
    if (!isConnected) {
      setErrorMsg(t('errorNotConnected'));
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
        setSuccessMsg(t('sent', { name: user.name }));
        setSearchInput('');
      } else {
        setErrorMsg(t('errorSend'));
      }
    } catch (err) {
      console.error(err);
      setErrorMsg(t('errorSend'));
    } finally {
      setIsSending(null);
    }
  };

  const hasScore = currentScore !== undefined && currentScore > 0;

  return (
    <Dialog
      open={isOpen}
      onClose={handleClose}
      title={hasScore ? t('titleShare') : t('title')}
    >
      <p className="text-muted mb-4 text-sm">
        {t.rich('description', {
          game: gameName,
          b: (chunks) => (
            <span className="text-on-elevated-surface font-bold">{chunks}</span>
          ),
        })}
      </p>

      {hasScore && (
        <label className="bg-sunken-surface border-elevated-border mb-4 grid cursor-pointer grid-cols-[1fr_auto] items-center gap-x-3 rounded-xl border p-3">
          <span className="text-on-elevated-surface text-xs font-bold">
            {t('bragTitle')}
          </span>
          <span className="relative row-span-2 inline-flex items-center">
            <input
              type="checkbox"
              checked={includeScore}
              onChange={(e) => setIncludeScore(e.target.checked)}
              className="peer sr-only"
            />
            <span className="bg-field peer-checked:bg-accent-teal peer-focus-visible:outline-accent-teal h-5 w-10 rounded-full after:absolute after:start-[2px] after:top-[2px] after:h-4 after:w-4 after:rounded-full after:border after:border-gray-300 after:bg-white after:transition-all after:content-[''] peer-checked:after:translate-x-full peer-checked:after:border-white peer-focus-visible:outline-2 peer-focus-visible:outline-offset-2 rtl:peer-checked:after:-translate-x-full" />
          </span>
          <span className="text-muted text-[11px]">
            {t.rich('includeScore', {
              score: currentScore,
              strong: (chunks) => (
                <strong className="text-accent-teal">{chunks}</strong>
              ),
            })}
          </span>
        </label>
      )}

      <TextField
        id="friend-search"
        type="search"
        placeholder={t('searchPlaceholder')}
        aria-label={t('searchLabel')}
        value={searchInput}
        onChange={(e) => setSearchInput(e.target.value)}
        className="mb-4"
        tone="chat"
      />

      {errorMsg && (
        <div
          role="alert"
          className="border-danger/20 bg-danger/10 text-danger mb-4 rounded-xl border p-2.5 text-xs font-medium"
        >
          {errorMsg}
        </div>
      )}
      <div role="status">
        {successMsg && (
          <div className="border-success/20 bg-success/10 text-success mb-4 rounded-xl border p-2.5 text-xs font-medium">
            {successMsg}
          </div>
        )}
      </div>

      <div className="flex max-h-60 flex-col gap-2 overflow-y-auto">
        {searchResults.length > 0 ? (
          searchResults.map((u) => (
            <div
              key={u.id}
              className="border-elevated-border bg-field/50 hover:bg-field flex items-center justify-between rounded-xl border p-3 transition-colors"
            >
              <div className="flex items-center gap-3">
                <Avatar
                  src={u.avatarId ? `/api/users/avatar/${u.avatarId}` : null}
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
                {isSending === u.id ? t('sending') : t('challenge')}
              </Button>
            </div>
          ))
        ) : (
          <p className="text-muted p-4 text-center text-sm">
            {debouncedQuery ? t('noResults') : t('typeToSearch')}
          </p>
        )}
      </div>

      <Button
        variant="neutral"
        onClick={handleClose}
        fullWidth
        className="mt-4"
      >
        {t('close')}
      </Button>
    </Dialog>
  );
}
