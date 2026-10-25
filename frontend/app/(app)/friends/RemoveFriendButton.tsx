'use client';

import { useRouter } from 'next/navigation';
import { useTranslations } from 'next-intl';

import { useAsyncAction } from '../../hooks/useAsyncAction';

import { removeFriendAction } from './actions';

interface Props {
  friendId: string;
  onRemoved: () => void;
}

export default function RemoveFriendButton({ friendId, onRemoved }: Props) {
  const t = useTranslations('friends');
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
        className="bg-row-action text-on-row-action px-4.5 py-2.25 rounded-[10px] text-sm font-bold transition-[filter] hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {isLoading ? t('removing') : t('remove')}
      </button>
      {error && <p className="text-danger text-xs">{error}</p>}
    </div>
  );
}
