'use client';

import { useEffect, useState } from 'react';
import { usePresence } from '../hooks/usePresence';

const RECONNECTING_MSG = 'Reconnecting to the server...';
const UNREACHABLE_MSG = 'Cannot reach the server. Retrying...';

const RECONNECT_GRACE_MS = 5000;
const INITIAL_GRACE_MS = 8000;

export default function ConnectionBanner() {
  const { isConnected } = usePresence();
  
  const [state, setState] = useState({
    wasConnected: false,
    message: null as string | null,
    prevIsConnected: isConnected,
  });

  if (isConnected !== state.prevIsConnected) {
    setState((prev) => ({
      wasConnected: prev.wasConnected || isConnected,
      message: null,
      prevIsConnected: isConnected,
    }));
  }

  useEffect(() => {
    if (isConnected) return;

    const delay = state.wasConnected ? RECONNECT_GRACE_MS : INITIAL_GRACE_MS;
    const text = state.wasConnected ? RECONNECTING_MSG : UNREACHABLE_MSG;

    const timer = setTimeout(() => {
      setState((prev) => ({ ...prev, message: text }));
    }, delay);

    return () => clearTimeout(timer);
  }, [isConnected, state.wasConnected]);

  if (isConnected || !state.message) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="bg-danger/10 border-danger/20 text-danger shrink-0 border-b p-2 text-center text-xs font-medium"
    >
      {state.message}
    </div>
  );
}
