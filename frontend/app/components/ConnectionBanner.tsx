'use client';

import { useEffect, useState } from 'react';
import { usePresence } from '../hooks/usePresence';

const CONNECTION_LOST_MSG =
  'Lost connection to the server. Attempting to recover session...';
const GRACE_PERIOD_MS = 3000;

export default function ConnectionBanner() {
  const { isConnected } = usePresence();
  const [hasEverConnected, setHasEverConnected] = useState(false);
  const [showBanner, setShowBanner] = useState(false);

  if (isConnected && !hasEverConnected) {
    setHasEverConnected(true);
  }

  if (isConnected && showBanner) {
    setShowBanner(false);
  }

  useEffect(() => {
    if (isConnected || !hasEverConnected) return;

    const timer = setTimeout(() => setShowBanner(true), GRACE_PERIOD_MS);
    return () => clearTimeout(timer);
  }, [isConnected, hasEverConnected]);

  if (!showBanner || isConnected) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="bg-danger/10 border-b border-danger/20 p-2 text-center text-xs font-medium text-danger shrink-0"
    >
      {CONNECTION_LOST_MSG}
    </div>
  );
}
