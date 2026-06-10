'use client';

import { useState } from 'react';
import { usePresence } from '../hooks/usePresence';
import { useChat } from '../hooks/useChat';

export default function HomePage() {
  const [chatId, setChatId] = useState('test-chat-id');
  const [messageContent, setMessageContent] = useState('');
  const [presenceUserId, setPresenceUserId] = useState('00000000-0000-0000-0000-000000000000');

  const { isConnected, onlineStatus, checkPresence } = usePresence([presenceUserId]);
  const { sendMessage, deleteMessage } = useChat();

  return (
    <div className="flex flex-col w-screen min-h-screen p-8 items-center gap-6 text-black dark:text-white">
      <h1 className="font-bold text-3xl">STOMP WebSocket Test</h1>

      <div className="flex items-center gap-2">
        <span className="font-semibold">Status połączenia:</span>
        {isConnected ? (
          <span className="text-green-500 font-bold">Połączono</span>
        ) : (
          <span className="text-red-500 font-bold">Brak połączenia</span>
        )}
      </div>

      <div className="flex w-full max-w-4xl gap-8">
        {/* Chat Test Section */}
        <div className="flex-1 border p-4 rounded bg-gray-100 dark:bg-gray-800">
          <h2 className="font-bold text-xl mb-4">Chat</h2>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              placeholder="Chat ID"
              value={chatId}
              onChange={(e) => setChatId(e.target.value)}
              className="border p-2 rounded text-black"
            />
            <input
              type="text"
              placeholder="Wiadomość"
              value={messageContent}
              onChange={(e) => setMessageContent(e.target.value)}
              className="border p-2 rounded text-black"
            />
            <button
              onClick={() => sendMessage(chatId, messageContent)}
              disabled={!isConnected || !messageContent}
              className="bg-blue-500 hover:bg-blue-600 text-white p-2 rounded disabled:opacity-50 transition-colors"
            >
              Wyślij wiadomość
            </button>
            <button
              onClick={() => deleteMessage('test-message-id')}
              disabled={!isConnected}
              className="bg-red-500 hover:bg-red-600 text-white p-2 rounded disabled:opacity-50 transition-colors"
            >
              Wyślij żądanie usunięcia
            </button>
          </div>
        </div>

        {/* Presence Test Section */}
        <div className="flex-1 border p-4 rounded bg-gray-100 dark:bg-gray-800">
          <h2 className="font-bold text-xl mb-4">Presence</h2>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              placeholder="User ID (UUID)"
              value={presenceUserId}
              onChange={(e) => setPresenceUserId(e.target.value)}
              className="border p-2 rounded text-black"
            />
            <button
              onClick={() => checkPresence(presenceUserId)}
              disabled={!isConnected}
              className="bg-purple-500 hover:bg-purple-600 text-white p-2 rounded disabled:opacity-50 transition-colors"
            >
              Sprawdź obecność (wysyłka)
            </button>
            
            <div className="mt-4">
              <h3 className="font-semibold border-b pb-2 mb-2">Status monitorowanego użytkownika:</h3>
              <div className="mt-2 text-lg">
                UUID: <span className="text-gray-500 text-sm">{presenceUserId}</span><br />
                Status: {onlineStatus[presenceUserId] === true ? (
                  <span className="text-green-500 font-bold">Online</span>
                ) : onlineStatus[presenceUserId] === false ? (
                  <span className="text-red-500 font-bold">Offline</span>
                ) : (
                  <span className="text-gray-500 italic">Nieznany (oczekiwanie...)</span>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
