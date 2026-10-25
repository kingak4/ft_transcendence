'use client';

import SockJS from 'sockjs-client';
import { StompSessionProvider } from 'react-stomp-hooks';
import React from 'react';

export default function StompProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const wsUrl = '/api/ws';

  return (
    <StompSessionProvider
      url=""
      webSocketFactory={() => new SockJS(wsUrl)}
      reconnectDelay={3000}
    >
      {children}
    </StompSessionProvider>
  );
}
