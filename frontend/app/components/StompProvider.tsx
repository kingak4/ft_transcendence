'use client';

import SockJS from 'sockjs-client';
import { StompSessionProvider } from 'react-stomp-hooks';
import React from 'react';

export default function StompProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  // NEXT_PUBLIC_ prefix required so Next.js embeds this value in the browser bundle.
  // SockJS starts with an HTTP negotiation, so the URL uses http:// (or https:// behind TLS).
  const wsUrl = process.env.NEXT_PUBLIC_WS_URL ?? 'http://localhost:5001/ws';

  return (
    <StompSessionProvider
      url=""
      webSocketFactory={() => new SockJS(wsUrl)}
      debug={(str) => console.log('[STOMP]', str)}
      onConnect={() => console.log('[STOMP] Connected successfully')}
      onDisconnect={() => console.log('[STOMP] Disconnected')}
    >
      {children}
    </StompSessionProvider>
  );
}
