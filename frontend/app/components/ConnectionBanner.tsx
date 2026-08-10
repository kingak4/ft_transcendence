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
      // `sticky top-0` keeps the status in view once the page scrolls, and pairs
      // with the `flex-col` restored in (app)/layout.tsx: this banner is the
      // first row of that column, so it must span the full width above the rail
      // rather than sit beside it. The z-index puts it over page content; the
      // narrow-width hamburger is `fixed z-50` too and later in DOM order, so it
      // still paints over this strip's left end. That is accepted - the message
      // is centred and stays readable, and both are only on screen at once while
      // the connection is down.
      className="bg-danger/10 border-danger/20 text-danger sticky top-0 z-50 shrink-0 border-b p-2 text-center text-xs font-medium"
    >
      {state.message}
    </div>
  );
}
