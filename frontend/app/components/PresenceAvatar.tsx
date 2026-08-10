import type { ComponentProps } from 'react';

import Avatar from './Avatar';

/**
 * An `Avatar` with an online dot overlaid.
 *
 * The dot deliberately does not live inside `Avatar`: presence arrives on a
 * separate channel from identity and changes on its own schedule, so this is
 * composition rather than widening `Avatar`'s responsibility.
 *
 * Props are derived from `Avatar` instead of being restated, so a new `Avatar`
 * prop flows through here without an edit.
 */
type Props = ComponentProps<typeof Avatar> & {
  online: boolean;
};

export default function PresenceAvatar({ online, ...avatarProps }: Props) {
  // Scale with the avatar, but never below a clickable-looking speck.
  const dotSize = Math.max(8, Math.round(avatarProps.size * 0.28));

  return (
    <div className="relative shrink-0">
      <Avatar {...avatarProps} />
      <span
        className={`${
          online ? 'bg-hub-online' : 'bg-gray-400'
        } ring-hub-panel absolute bottom-0 right-0 rounded-full ring-2`}
        style={{ width: dotSize, height: dotSize }}
      >
        <span className="sr-only">{online ? 'Online' : 'Offline'}</span>
      </span>
    </div>
  );
}
