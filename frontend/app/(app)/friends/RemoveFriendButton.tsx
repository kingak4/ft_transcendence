'use client';

import { useRouter } from 'next/navigation';

import { useAsyncAction } from '../../hooks/useAsyncAction';

import { removeFriendAction } from './actions';

interface Props {
  friendId: string;
  onRemoved: () => void;
}

// This wears the export's row-button treatment (isSearch, line 318): 9x18px,
// 10px radius, 13.5px at 700, a pale #eaf6fb -> #e9f9f0 gradient and #146b7a
// text. In the export that styling belongs to its "Open chat" action; here it
// marks the secondary one, with the accent going to OpenChatLink beside it.
//
// The text colour is `hub-teal`, which IS that #146b7a - a token, not a
// literal. Only the gradient is an arbitrary value, for the same reason Card's
// is: no Tailwind scale expresses a two-stop pale gradient, and 12.0 keeps such
// values exact rather than rounding them.
//
// The export's hover is a flat #d2e9f6, which a gradient cannot transition to;
// `brightness-95` moves in the same direction. Recorded in 12.5.
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
        className="text-hub-teal px-4.5 py-2.25 rounded-[10px] bg-[linear-gradient(135deg,#eaf6fb,#e9f9f0)] text-sm font-bold transition-[filter] hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? 'Removing…' : 'Remove'}
      </button>
      {error && <p className="text-danger text-xs">{error}</p>}
    </div>
  );
}
