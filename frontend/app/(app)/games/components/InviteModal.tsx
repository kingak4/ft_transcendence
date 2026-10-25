'use client';

import { useTranslations } from 'next-intl';
import { useState, useEffect } from 'react';
import { useChat } from '../../../hooks/useChat';
import { client } from '../../../lib/api-clients';
import Button from '../../../components/Button';
import TextField from '../../../components/TextField';
import Avatar from '../../../components/Avatar';
import Icon from '../../../components/Icon';
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

  if (!isOpen) return null;

  return (
    <div className="animate-in fade-in fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm duration-200">
      <div className="bg-elevated-surface border-elevated-border w-full max-w-md rounded-2xl border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-on-elevated-surface text-xl font-bold">
            {currentScore !== undefined && currentScore > 0
              ? t('titleShare')
              : t('title')}
          </h2>
          <button
            onClick={handleClose}
            className="text-muted hover:text-on-elevated-surface cursor-pointer rounded-lg p-1 transition-colors"
            aria-label={t('close')}
          >
            <Icon name="x" size="sm" />
          </button>
        </div>

        <p className="text-muted mb-4 text-sm">
          {t.rich('description', {
            game: gameName,
            b: (chunks) => (
              <span className="text-on-elevated-surface font-bold">
                {chunks}
              </span>
            ),
          })}
        </p>

        {currentScore !== undefined && currentScore > 0 && (
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
              <div className="bg-field peer-checked:bg-accent-teal peer h-5 w-10 rounded-full after:absolute after:left-[2px] after:top-[2px] after:h-4 after:w-4 after:rounded-full after:border after:border-gray-300 after:bg-white after:transition-all after:content-[''] peer-checked:after:translate-x-full peer-checked:after:border-white peer-focus:outline-none"></div>
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
                  {isSending === u.id ? t('sending') : t('challenge')}
                </Button>
              </div>
            ))
          ) : debouncedQuery ? (
            <div className="text-muted p-4 text-center text-sm">
              {t('noResults')}
            </div>
          ) : (
            <div className="text-muted p-4 text-center text-sm opacity-60">
              {t('typeToSearch')}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
