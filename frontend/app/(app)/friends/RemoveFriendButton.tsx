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
// Step 5 unit 5.5 made the gradient `bg-hub-row-action` and moved the label off
// `hub-teal` onto `hub-on-row-action`. The second half is the less obvious one:
// `hub-teal` is a FILL hue - it is one stop of the message bubble - and this
// label sits on top of a fill built from the same family. Sharing one token
// meant that under Mocha the pale gradient stayed pale while the text followed
// the flavour, so the two halves of one button themed at different rates. A
// label on a fill needs its own role. Its default value is the same #146b7a.
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
        className="bg-hub-row-action text-hub-on-row-action px-4.5 py-2.25 rounded-[10px] text-sm font-bold transition-[filter] hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? 'Removing…' : 'Remove'}
      </button>
      {error && <p className="text-danger text-xs">{error}</p>}
    </div>
  );
}
