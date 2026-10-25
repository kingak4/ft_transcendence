'use client';

import { useState, useEffect, useMemo, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useChat, useChatSubscription } from '@/app/hooks/useChat';
import { usePresence } from '@/app/hooks/usePresence';
import { client } from '@/app/lib/api-clients';
import Announcer, { type Announcement } from '@/app/components/Announcer';
import MessageBubble from './MessageBubble';
import Composer from './Composer';
import { ChatMessage, ChatUser, BackendChatMessage } from './types';

// Error state holds a key, not text, so effects don't depend on the translator.
type ChatErrorKey =
  | 'errorInitChat'
  | 'errorSendNotConnected'
  | 'errorDeleteNotConnected';

interface Props {
  myUserId: string | null;
  user: ChatUser | null;
  initialChatId: string | null;
}

export default function ChatInterface({
  myUserId,
  user,
  initialChatId,
}: Props) {
  const t = useTranslations('chat.messages');
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [chatId, setChatId] = useState<string | null>(null);
  const [inputValue, setInputValue] = useState('');
  const [errorKey, setErrorKey] = useState<ChatErrorKey | null>(null);
  const [announcement, setAnnouncement] = useState<Announcement | null>(null);
  const messagesListRef = useRef<HTMLDivElement>(null);
  const creationPromiseRef = useRef<{
    friendId: string;
    promise: Promise<string>;
  } | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const router = useRouter();

  const { sendMessage, deleteMessage } = useChat();

  const userId = user?.id;
  const userName = user?.name ?? '';

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

        if (!currentChatId) {
          if (creationPromiseRef.current?.friendId !== userId) {
            const promise = client.POST('/chats/{recipientId}', {
              params: { path: { recipientId: userId } },
            }).then(({ data, response }) => {
              if (response.ok && data) {
                return data.chatId as string;
              }
              throw new Error('Failed to create/get chat');
            }).catch((err) => {
              if (creationPromiseRef.current?.friendId === userId) {
                creationPromiseRef.current = null;
              }
              setErrorKey('errorInitChat');
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
          params: { path: { chatId: currentChatId }, query: { page: 0, size: 1000 } },
        });

        if (msgsRes.ok && msgsData && active) {
          const historyMessages: ChatMessage[] = msgsData
            .map((msg: BackendChatMessage) => ({
              messageId: msg.messageId || '',
              senderId: msg.senderId || '',
              content: msg.content || '',
              time: msg.createdAt || '',
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
    if (newMessage.content !== undefined && newMessage.senderId !== myUserId) {
      setAnnouncement({
        id: newMessage.messageId,
        text: t('newMessageFrom', {
          name: userName,
          content: newMessage.content,
        }),
      });
    }
    setMessages((prev) => {
      if (newMessage.content === undefined && newMessage.time === undefined) {
        return prev.map((m) =>
          m.messageId === newMessage.messageId
            ? { ...m, isDeleted: true }
            : m,
        );
      }

      if (prev.find((m) => m.messageId === newMessage.messageId)) return prev;
      return [
        ...prev,
        {
          messageId: newMessage.messageId,
          senderId: newMessage.senderId,
          content: newMessage.content || '',
          time: newMessage.time,
        },
      ];
    });
  });

  useEffect(() => {
    const list = messagesListRef.current;
    if (!list) return;
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    list.scrollTo({ top: list.scrollHeight, behavior: reduceMotion ? 'auto' : 'smooth' });
  }, [messages]);

  useEffect(() => {
    if (chatId) {
      inputRef.current?.focus({ preventScroll: true });
    }
  }, [chatId]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() === '' || !chatId) return;
    const success = sendMessage(chatId, inputValue);
    if (!success) {
      setErrorKey('errorSendNotConnected');
    } else {
      setErrorKey(null);
      setInputValue('');
    }
  };

  return (
    <>
      <div
        ref={messagesListRef}
        tabIndex={0}
        role="region"
        aria-label={t('label')}
        className="relative flex min-h-0 flex-1 flex-col gap-4 overflow-y-auto p-6 focus-visible:-outline-offset-2"
      >
        {messages.map((message) => (
          <MessageBubble
            key={message.messageId}
            message={message}
            isMine={message.senderId === myUserId}
            senderName={
              message.senderId === myUserId
                ? t('you')
                : userName
            }
            onDelete={() => {
              if (chatId) {
                if (!deleteMessage(chatId, message.messageId)) {
                  setErrorKey('errorDeleteNotConnected');
                  return;
                }
                setErrorKey(null);
                setMessages((prev) =>
                  prev.map((m) =>
                    m.messageId === message.messageId
                      ? { ...m, isDeleted: true }
                      : m,
                  ),
                );
              }
            }}
          />
        ))}
      </div>

      <Composer
        ref={inputRef}
        inputValue={inputValue}
        setInputValue={setInputValue}
        handleSend={handleSend}
        isInputDisabled={!chatId}
        isButtonDisabled={!chatId || inputValue.trim() === ''}
        errorMsg={errorKey && t(errorKey)}
      />
      <Announcer announcement={announcement} />
    </>
  );
}
