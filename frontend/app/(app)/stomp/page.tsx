'use client';

import { useState } from 'react';

import Button from '../../components/Button';

import { usePresence } from '../../hooks/usePresence';
// import { useChat } from '../../hooks/useChat';

const PANEL_CLASSES =
  'bg-inverse-surface text-on-inverse-surface border-on-inverse-surface/15 flex-1 rounded-2xl border p-4';

const INPUT_CLASSES =
  'text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary w-full rounded-lg bg-on-inverse-surface/10 px-4 py-2 text-sm outline-none focus:ring-1';

export default function HomePage() {
  // const [chatId, setChatId] = useState('test-chat-id');
  // const [messageContent, setMessageContent] = useState('');
  const [presenceUserId, setPresenceUserId] = useState(
    '00000000-0000-0000-0000-000000000000',
  );

  const { isConnected, onlineStatus, checkPresence } = usePresence([
    presenceUserId,
  ]);
  // const { sendMessage, deleteMessage } = useChat();

  return (
    <div className="flex h-full w-full flex-col items-center gap-6 p-8">
      <h1 className="text-3xl font-bold">STOMP WebSocket Test</h1>

      <div className="flex items-center gap-2">
        <span className="font-semibold">Status połączenia:</span>
        {isConnected ? (
          <span className="text-success font-bold">Połączono</span>
        ) : (
          <span className="text-danger font-bold">Brak połączenia</span>
        )}
      </div>

      {/* <div className="flex w-full max-w-4xl gap-8">
        {}
        <div className={PANEL_CLASSES}>
          <h2 className="mb-4 text-xl font-bold">Chat</h2>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              placeholder="Chat ID"
              value={chatId}
              onChange={(e) => setChatId(e.target.value)}
              className={INPUT_CLASSES}
            />
            <input
              type="text"
              placeholder="Wiadomość"
              value={messageContent}
              onChange={(e) => setMessageContent(e.target.value)}
              className={INPUT_CLASSES}
            />
            <Button
              onClick={() => sendMessage(chatId, messageContent)}
              disabled={!isConnected || !messageContent}
            >
              Wyślij wiadomość
            </Button>
            <Button
              variant="outline"
              onClick={() => deleteMessage('test-message-id')}
              disabled={!isConnected}
            >
              Wyślij żądanie usunięcia
            </Button>
          </div>
        </div> */}

      {/* Presence Test Section */}
      <div className={PANEL_CLASSES}>
        <h2 className="mb-4 text-xl font-bold">Presence</h2>
        <div className="flex flex-col gap-3">
          <input
            type="text"
            placeholder="User ID (UUID)"
            value={presenceUserId}
            onChange={(e) => setPresenceUserId(e.target.value)}
            className={INPUT_CLASSES}
          />
          <Button
            onClick={() => checkPresence(presenceUserId)}
            disabled={!isConnected}
          >
            Sprawdź obecność (wysyłka)
          </Button>

          <div className="mt-4">
            <h3 className="border-on-inverse-surface/15 mb-2 border-b pb-2 font-semibold">
              Status monitorowanego użytkownika:
            </h3>
            <div className="mt-2 text-lg">
              UUID:{' '}
              <span className="text-on-inverse-surface/60 text-sm">
                {presenceUserId}
              </span>
              <br />
              Status:{' '}
              {onlineStatus[presenceUserId] === true ? (
                <span className="text-success font-bold">Online</span>
              ) : onlineStatus[presenceUserId] === false ? (
                <span className="text-danger font-bold">Offline</span>
              ) : (
                <span className="text-on-inverse-surface/60 italic">
                  Nieznany (oczekiwanie...)
                </span>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
    // </div >
  );
}
