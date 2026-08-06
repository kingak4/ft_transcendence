'use client';

import { usePresence } from '../hooks/usePresence';

const CONNECTION_LOST_MSG = "Lost connection to the server. Attempting to recover session...";

export default function ConnectionBanner() {
  const { isConnected } = usePresence();

  if (isConnected) return null;

  return (
    <div className="bg-danger/10 border-b border-danger/20 p-2 text-center text-xs font-medium text-danger shrink-0">
      {CONNECTION_LOST_MSG}
    </div>
  );
}
