import { useCallback } from 'react';
import { useStompClient, useSubscription } from 'react-stomp-hooks';

const APP_PREFIX = '/transcend';

export type ChatEventPayload = {
  messageId: string;
  senderId: string;
  content?: string;
  time?: string;
};

export function useChat() {
  const stompClient = useStompClient();

  const sendMessage = useCallback((chatId: string, content: string) => {
    if (stompClient) {
      const request = { content };
      stompClient.publish({
        destination: `${APP_PREFIX}/chat/${chatId}/send`,
        body: JSON.stringify(request),
      });
    } else {
      console.warn('STOMP client is not connected.');
    }
  }, [stompClient]);

  const deleteMessage = useCallback((chatId: string, messageId: string) => {
    if (stompClient) {
      const request = {};
      stompClient.publish({
        destination: `${APP_PREFIX}/chat/${chatId}/messages/${messageId}/delete`,
        body: JSON.stringify(request),
      });
    } else {
      console.warn('STOMP client is not connected.');
    }
  }, [stompClient]);

  return { sendMessage, deleteMessage, isConnected: !!stompClient };
}

export function useChatSubscription(chatId: string, onMessage: (message: ChatEventPayload) => void) {
  const destination = `/topic/chat/${chatId}/messages`;
  
  useSubscription(chatId ? destination : [], (message) => {
    try {
      const parsed = JSON.parse(message.body) as ChatEventPayload;
      onMessage(parsed);
    } catch (err) {
      console.error('Failed to parse chat message', err);
    }
  });
}
