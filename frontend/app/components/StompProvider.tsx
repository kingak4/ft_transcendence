'use client';

import { StompSessionProvider } from 'react-stomp-hooks';
import React from 'react';

export default function StompProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  // Use NEXT_PUBLIC_WS_URL if available, otherwise default to localhost:8080/ws
  // Important: In Next.js, env variables used in client components must be prefixed with NEXT_PUBLIC_
  const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080/ws';

  return (
    <StompSessionProvider
      url={wsUrl}
      // You can remove debug in production or configure it to only log in dev
      debug={(str) => console.log('[STOMP]', str)}
      onConnect={() => console.log('[STOMP] Connected successfully')}
      onDisconnect={() => console.log('[STOMP] Disconnected')}
    >
      {children}
    </StompSessionProvider>
  );
}
