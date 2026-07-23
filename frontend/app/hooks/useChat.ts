import { useStompClient, useSubscription } from 'react-stomp-hooks';
import { useCallback } from 'react';

const APP_PREFIX = '/transcend';
const TOPIC_PREFIX = '/topic';

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

    return { sendMessage, deleteMessage };
}

export type ChatEventPayload = {
    messageId: string;
    senderId: string;
    content?: string;
    time?: string;
};

export function useChatSubscription(chatId: string, onEvent: (event: ChatEventPayload) => void) {
    useSubscription(`${TOPIC_PREFIX}/chat/${chatId}/messages`, (message) => {
        try {
            const payload = JSON.parse(message.body) as ChatEventPayload;
            onEvent(payload);
        } catch (error) {
            console.error('Błąd przy odbieraniu zdarzenia z czatu:', error);
        }
    });
}
