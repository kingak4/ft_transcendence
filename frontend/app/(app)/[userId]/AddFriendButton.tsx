'use client';

import { useRouter } from 'next/navigation';

import { addFriendAction } from './actions';
import { useAsyncAction } from './useAsyncAction';

interface Props {
  friendId: string;
  onAdded: () => void;
}

export default function AddFriendButton({ friendId, onAdded }: Props) {
  const router = useRouter();
  const { isLoading, error, run } = useAsyncAction();

  function handleClick() {
    run(
      () => addFriendAction(friendId),
      () => {
        onAdded();
        router.refresh();
      },
    );
  }

  return (
    <div className="flex flex-col items-end gap-1">
      <button
        onClick={handleClick}
        disabled={isLoading}
        className="bg-primary text-on-primary rounded-lg px-3 py-1.5 text-xs font-bold transition-colors hover:brightness-125 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? 'Adding…' : 'Add'}
      </button>
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}
