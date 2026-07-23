'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import { addFriendAction } from './actions';

interface Props {
  friendId: string;
  onAdded: () => void;
}

export default function AddFriendButton({ friendId, onAdded }: Props) {
  const router = useRouter();
  const [isAdding, setIsAdding] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleClick() {
    setIsAdding(true);
    setError(null);

    try {
      const result = await addFriendAction(friendId);
      if (!result.success) {
        setError(result.message);
        return;
      }
      onAdded();
      router.refresh();
    } finally {
      setIsAdding(false);
    }
  }

  return (
    <div className="flex flex-col items-end gap-1">
      <button
        onClick={handleClick}
        disabled={isAdding}
        className="bg-brand-secondary-color text-brand-additional-color-2 rounded-lg px-3 py-1.5 text-xs font-bold transition-colors hover:brightness-125 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isAdding ? 'Adding…' : 'Add'}
      </button>
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}
