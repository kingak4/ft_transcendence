import { useState } from 'react';
import { useStompClient, useSubscription } from 'react-stomp-hooks';

const APP_PREFIX = '/transcend';
const TOPIC_PREFIX = '/topic';

export function usePresence(userIdsToWatch: string[] = []) {
  const stompClient = useStompClient();
  const [onlineStatus, setOnlineStatus] = useState<Record<string, boolean>>({});

  // W backendzie topic dla konkretnego usera to: /topic/user/{userId}/presence
  const destinations = userIdsToWatch.map(
    (id) => `${TOPIC_PREFIX}/user/${id}/presence`,
  );

  useSubscription(destinations, (message) => {
    try {
      // Backend wysyła klasę PresenceStatusResponse: { userId: UUID, isOnline: boolean }
      const payload = JSON.parse(message.body);

      if (payload.userId !== undefined && payload.isOnline !== undefined) {
        setOnlineStatus((prev) => ({
          ...prev,
          [payload.userId]: payload.isOnline,
        }));
      }
    } catch (e) {
      console.error('Failed to parse presence event', e);
    }
  });

  const checkPresence = (userId: string) => {
    if (stompClient) {
      const request = { userId };
      stompClient.publish({
        destination: `${APP_PREFIX}/presence/check`,
        body: JSON.stringify(request),
      });
    } else {
      console.warn('STOMP client is not connected.');
    }
  };

  return { checkPresence, onlineStatus, isConnected: !!stompClient };
}
