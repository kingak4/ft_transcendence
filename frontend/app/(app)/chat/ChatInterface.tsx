'use client';

import { useState, useEffect, useMemo, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useChat, useChatSubscription } from '@/app/hooks/useChat';
import { usePresence } from '@/app/hooks/usePresence';
import { client } from '@/app/lib/api-clients';
import MessageBubble from './MessageBubble';
import Composer from './Composer';
import { ChatMessage, ChatUser, BackendChatMessage } from './types';
import { CHAT_DICT } from './dictionary';

const DELETED_MESSAGE_TEXT = CHAT_DICT.chat.messageDeleted;

interface Props {
  myUserId: string | null;
  user: ChatUser | null;
  initialChatId: string | null;
}

export default function ChatInterface({ myUserId, user, initialChatId }: Props) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [chatId, setChatId] = useState<string | null>(null);
  const [inputValue, setInputValue] = useState('');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const creationPromiseRef = useRef<{ friendId: string; promise: Promise<string> } | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const router = useRouter();

  const { sendMessage, deleteMessage } = useChat();

  const userId = user?.id;

  const monitoredUserIds = useMemo(() => {
    return userId ? [userId] : [];
  }, [userId]);

  const { checkPresence, isConnected } = usePresence(monitoredUserIds);

  useEffect(() => {
    if (isConnected && monitoredUserIds.length > 0) {
      monitoredUserIds.forEach((id) => checkPresence(id));
    }
  }, [monitoredUserIds, isConnected, checkPresence]);

  useEffect(() => {
    let active = true;

    async function initChat() {
      if (!userId) {
        setChatId(null);
        setMessages([]);
        return;
      }
      try {
        let currentChatId = initialChatId;
        
        // If we don't know the chat ID yet, create it.
        if (!currentChatId) {
          if (creationPromiseRef.current?.friendId !== userId) {
            const promise = client.POST('/chats/{recipientId}', {
              params: { path: { recipientId: userId } },
            }).then(({ data, response }) => {
              if (response.ok && data) {
                return data.chatId as string;
              }
              throw new Error(CHAT_DICT.chat.errorCreateChat);
            }).catch((err) => {
              if (creationPromiseRef.current?.friendId === userId) {
                creationPromiseRef.current = null;
              }
              setErrorMsg(CHAT_DICT.chat.errorInitChat);
              throw err;
            });
            creationPromiseRef.current = { friendId: userId, promise };
          }

          try {
            currentChatId = await creationPromiseRef.current.promise;
            router.refresh();
          } catch (e) {
            console.error(e);
            return;
          }
        }
        
        if (!active) return;
        setChatId(currentChatId);

        const { data: msgsData, response: msgsRes } = await client.GET('/chats/{chatId}/messages', {
          params: { path: { chatId: currentChatId }, query: { page: 0, size: 50 } },
        });

          if (msgsRes.ok && msgsData && active) {
            const historyMessages: ChatMessage[] = msgsData
              .map((msg: BackendChatMessage) => ({
                messageId: msg.messageId || '',
                senderId: msg.senderId || '',
                content: msg.content || '',
                time: msg.createdAt || '', // Optional format later
              }))
              .reverse();
            setMessages(historyMessages);
          }
      } catch (e) {
        console.error('Error initializing chat:', e);
      }
    }

    initChat();
    return () => { active = false; };
  }, [userId, initialChatId, router]);

  useChatSubscription(chatId || '', (newMessage) => {
    if (!chatId) return;
    setMessages((prev) => {
        if (newMessage.content === undefined && newMessage.time === undefined) {
          return prev.map((m) => 
            m.messageId === newMessage.messageId
              ? { ...m, content: DELETED_MESSAGE_TEXT, isDeleted: true }
              : m
          );
        }

      // Basic deduplication
      if (prev.find((m) => m.messageId === newMessage.messageId)) return prev;
      return [...prev, {
        messageId: newMessage.messageId,
        senderId: newMessage.senderId,
        content: newMessage.content || '',
        time: newMessage.time,
      }];
    });
  });

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    if (chatId) {
      inputRef.current?.focus();
    }
  }, [chatId]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() === '' || !chatId) return;
    const success = sendMessage(chatId, inputValue);
    if (!success) {
      setErrorMsg(CHAT_DICT.chat.errorSendNotConnected);
    } else {
      setErrorMsg(null);
      setInputValue('');
    }
  };

  return (
    <>
      {/* Messages List */}
      <div className="flex min-h-0 flex-1 flex-col gap-4 overflow-y-auto p-6">
        {messages.map((message) => (
          <MessageBubble
            key={message.messageId}
            message={message}
            isMine={message.senderId === myUserId}
            onDelete={() => {
              if (chatId) {
                if (!deleteMessage(chatId, message.messageId)) {
                  setErrorMsg(CHAT_DICT.chat.errorDeleteNotConnected);
                  return;
                }
                setErrorMsg(null);
                // Optimistic UI update
                setMessages((prev) => prev.map((m) => 
                  m.messageId === message.messageId
                    ? { ...m, content: DELETED_MESSAGE_TEXT, isDeleted: true }
                    : m
                ));
              }
            }}
          />
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Composer */}
      <Composer
        ref={inputRef}
        inputValue={inputValue}
        setInputValue={setInputValue}
        handleSend={handleSend}
        isInputDisabled={!chatId}
        isButtonDisabled={!chatId || inputValue.trim() === ''}
        errorMsg={errorMsg}
      />
    </>
  );
}
