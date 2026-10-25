'use client';

import { useTranslations } from 'next-intl';
import { useEffect, useState } from 'react';
import { usePresence } from '../hooks/usePresence';

type BannerMessage = 'reconnecting' | 'unreachable';

const RECONNECT_GRACE_MS = 5000;
const INITIAL_GRACE_MS = 8000;

export default function ConnectionBanner() {
  const t = useTranslations('connection');
  const { isConnected } = usePresence();

  // Holds a message key, so a language switch re-translates a visible banner.
  const [state, setState] = useState({
    wasConnected: false,
    message: null as BannerMessage | null,
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
    const message: BannerMessage = state.wasConnected
      ? 'reconnecting'
      : 'unreachable';

    const timer = setTimeout(() => {
      setState((prev) => ({ ...prev, message }));
    }, delay);

    return () => clearTimeout(timer);
  }, [isConnected, state.wasConnected]);

  if (isConnected || !state.message) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="bg-danger/10 border-danger/20 text-danger sticky top-0 z-50 shrink-0 border-b p-2 text-center text-xs font-medium"
    >
      {t(state.message)}
    </div>
  );
}
