'use client';

import { useEffect, useRef, useState } from 'react';
import { usePresence } from '../hooks/usePresence';

const RECONNECTING_MSG = 'Reconnecting to the server...';
const UNREACHABLE_MSG = 'Cannot reach the server. Retrying...';

const RECONNECT_GRACE_MS = 5000;
const INITIAL_GRACE_MS = 8000;

export default function ConnectionBanner() {
  const { isConnected } = usePresence();
  const hasEverConnected = useRef(false);
  const [showBanner, setShowBanner] = useState(false);

  useEffect(() => {
    if (isConnected) {
      hasEverConnected.current = true;
      setShowBanner(false);
      return;
    }

    const delay = hasEverConnected.current
      ? RECONNECT_GRACE_MS
      : INITIAL_GRACE_MS;
      
    const timer = setTimeout(() => setShowBanner(true), delay);
    return () => clearTimeout(timer);
  }, [isConnected]);

  if (isConnected || !showBanner) return null;

  const message = hasEverConnected.current ? RECONNECTING_MSG : UNREACHABLE_MSG;

  return (
    <div
      role="status"
      aria-live="polite"
      className="bg-danger/10 border-danger/20 text-danger shrink-0 border-b p-2 text-center text-xs font-medium"
    >
      {message}
    </div>
  );
}
