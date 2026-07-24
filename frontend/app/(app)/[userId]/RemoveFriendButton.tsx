'use client';

import { useRouter } from 'next/navigation';

import { removeFriendAction } from './actions';
import { useAsyncAction } from './useAsyncAction';

interface Props {
  friendId: string;
  onRemoved: () => void;
}

export default function RemoveFriendButton({ friendId, onRemoved }: Props) {
  const router = useRouter();
  const { isLoading, error, run } = useAsyncAction();

  function handleClick() {
    run(
      () => removeFriendAction(friendId),
      () => {
        onRemoved();
        router.refresh();
      },
    );
  }

  return (
    <div className="flex flex-col items-end gap-1">
      <button
        onClick={handleClick}
        disabled={isLoading}
        className="text-brand-main-color/60 rounded-lg bg-white/10 px-3 py-1.5 text-xs font-medium transition-colors hover:bg-white/20 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? 'Removing…' : 'Remove'}
      </button>
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}
