'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import { removeFriendAction } from './actions';

interface Props {
  friendId: string;
}

export default function RemoveFriendButton({ friendId }: Props) {
  const router = useRouter();
  const [isRemoving, setIsRemoving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleClick() {
    setIsRemoving(true);
    setError(null);

    try {
      const result = await removeFriendAction(friendId);
      if (!result.success) {
        setError(result.message);
        return;
      }
      router.refresh();
    } finally {
      setIsRemoving(false);
    }
  }

  return (
    <div className="flex flex-col items-end gap-1">
      <button
        onClick={handleClick}
        disabled={isRemoving}
        className="text-on-inverse-surface/60 bg-on-inverse-surface/10 hover:bg-on-inverse-surface/20 rounded-lg px-3 py-1.5 text-xs font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isRemoving ? 'Removing…' : 'Remove'}
      </button>
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}
