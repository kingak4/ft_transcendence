'use client';

import { useRouter } from 'next/navigation';

import { useAsyncAction } from '../../hooks/useAsyncAction';

import { removeFriendAction } from './actions';

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
        className="text-on-elevated-surface/60 bg-on-elevated-surface/10 hover:bg-on-elevated-surface/20 rounded-lg px-3 py-1.5 text-xs font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? 'Removing…' : 'Remove'}
      </button>
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}
