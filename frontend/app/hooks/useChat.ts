import { useStompClient } from 'react-stomp-hooks';

const APP_PREFIX = '/transcend';

export function useChat() {
  const stompClient = useStompClient();

  const sendMessage = (chatId: string, content: string) => {
    if (stompClient) {
      const request = { content };
      stompClient.publish({
        destination: `${APP_PREFIX}/chat/${chatId}/send`,
        body: JSON.stringify(request),
      });
    } else {
      console.warn('STOMP client is not connected.');
    }
  };

  const deleteMessage = (messageId: string) => {
    if (stompClient) {
      const request = {};
      stompClient.publish({
        destination: `${APP_PREFIX}/chat/messages/${messageId}/delete`,
        body: JSON.stringify(request),
      });
    } else {
      console.warn('STOMP client is not connected.');
    }
  };

  return { sendMessage, deleteMessage, isConnected: !!stompClient };
}
