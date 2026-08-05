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
 *
 * TODO(stomp): `online` is a static fixture flag today. It should come from
 * the presence channel (`CheckPresenceRequest` / `app/hooks/usePresence.ts`),
 * which is a *separate* feed from chat messages - do not fold it into the
 * chat subscription. Keep this component presentational: subscribe in a
 * parent or a hook and pass the boolean down, so it stays renderable from a
 * Server Component and testable without a live socket.
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
      {online && (
        // The ring punches a panel-coloured gap so the dot reads as separate
        // from the avatar rather than as a bite out of its edge.
        <span
          className="bg-hub-online ring-hub-panel absolute bottom-0 right-0 rounded-full ring-2"
          style={{ width: dotSize, height: dotSize }}
        >
          <span className="sr-only">Online</span>
        </span>
      )}
    </div>
  );
}
