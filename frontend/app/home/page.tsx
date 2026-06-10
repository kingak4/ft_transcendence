'use client';

import { useState } from 'react';
import { usePresence } from '../hooks/usePresence';
import { useChat } from '../hooks/useChat';

export default function HomePage() {
  const [chatId, setChatId] = useState('test-chat-id');
  const [messageContent, setMessageContent] = useState('');
  const [presenceUserId, setPresenceUserId] = useState(
    '00000000-0000-0000-0000-000000000000',
  );

  const { isConnected, onlineStatus, checkPresence } = usePresence([
    presenceUserId,
  ]);
  const { sendMessage, deleteMessage } = useChat();

  return (
    <div className="flex min-h-screen w-screen flex-col items-center gap-6 p-8 text-black dark:text-white">
      <h1 className="text-3xl font-bold">STOMP WebSocket Test</h1>

      <div className="flex items-center gap-2">
        <span className="font-semibold">Status połączenia:</span>
        {isConnected ? (
          <span className="font-bold text-green-500">Połączono</span>
        ) : (
          <span className="font-bold text-red-500">Brak połączenia</span>
        )}
      </div>

      <div className="flex w-full max-w-4xl gap-8">
        {/* Chat Test Section */}
        <div className="flex-1 rounded border bg-gray-100 p-4 dark:bg-gray-800">
          <h2 className="mb-4 text-xl font-bold">Chat</h2>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              placeholder="Chat ID"
              value={chatId}
              onChange={(e) => setChatId(e.target.value)}
              className="rounded border p-2 text-black"
            />
            <input
              type="text"
              placeholder="Wiadomość"
              value={messageContent}
              onChange={(e) => setMessageContent(e.target.value)}
              className="rounded border p-2 text-black"
            />
            <button
              onClick={() => sendMessage(chatId, messageContent)}
              disabled={!isConnected || !messageContent}
              className="rounded bg-blue-500 p-2 text-white transition-colors hover:bg-blue-600 disabled:opacity-50"
            >
              Wyślij wiadomość
            </button>
            <button
              onClick={() => deleteMessage('test-message-id')}
              disabled={!isConnected}
              className="rounded bg-red-500 p-2 text-white transition-colors hover:bg-red-600 disabled:opacity-50"
            >
              Wyślij żądanie usunięcia
            </button>
          </div>
        </div>

        {/* Presence Test Section */}
        <div className="flex-1 rounded border bg-gray-100 p-4 dark:bg-gray-800">
          <h2 className="mb-4 text-xl font-bold">Presence</h2>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              placeholder="User ID (UUID)"
              value={presenceUserId}
              onChange={(e) => setPresenceUserId(e.target.value)}
              className="rounded border p-2 text-black"
            />
            <button
              onClick={() => checkPresence(presenceUserId)}
              disabled={!isConnected}
              className="rounded bg-purple-500 p-2 text-white transition-colors hover:bg-purple-600 disabled:opacity-50"
            >
              Sprawdź obecność (wysyłka)
            </button>

            <div className="mt-4">
              <h3 className="mb-2 border-b pb-2 font-semibold">
                Status monitorowanego użytkownika:
              </h3>
              <div className="mt-2 text-lg">
                UUID:{' '}
                <span className="text-sm text-gray-500">{presenceUserId}</span>
                <br />
                Status:{' '}
                {onlineStatus[presenceUserId] === true ? (
                  <span className="font-bold text-green-500">Online</span>
                ) : onlineStatus[presenceUserId] === false ? (
                  <span className="font-bold text-red-500">Offline</span>
                ) : (
                  <span className="italic text-gray-500">
                    Nieznany (oczekiwanie...)
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
