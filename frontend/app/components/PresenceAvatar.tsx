import { useTranslations } from 'next-intl';
import type { ComponentProps } from 'react';

import Avatar from './Avatar';

type Props = ComponentProps<typeof Avatar> & {
  online: boolean;
};

export default function PresenceAvatar({ online, ...avatarProps }: Props) {
  const t = useTranslations('common');
  const dotSize = Math.max(8, Math.round(avatarProps.size * 0.28));

  return (
    <div className="relative shrink-0">
      <Avatar {...avatarProps} />
      <span
        className={`${
          online ? 'bg-online' : 'bg-offline'
        } ring-elevated-surface absolute bottom-0 end-0 rounded-full ring-2`}
        style={{ width: dotSize, height: dotSize }}
      >
        <span className="sr-only">{online ? t('online') : t('offline')}</span>
      </span>
    </div>
  );
}
