import { useTranslations } from 'next-intl';

import type { ValidationKey } from '../lib/validation/messages';
import {
  AVATAR_MAX_BYTES,
  CHAT_MESSAGE_MAX,
  DISPLAY_NAME_MAX,
  DISPLAY_NAME_MIN,
  PASSWORD_MAX,
  PASSWORD_MIN,
} from '../lib/validation/rules';

const VALIDATION_LIMITS = {
  passwordMin: PASSWORD_MIN,
  passwordMax: PASSWORD_MAX,
  displayNameMin: DISPLAY_NAME_MIN,
  displayNameMax: DISPLAY_NAME_MAX,
  avatarMaxMb: AVATAR_MAX_BYTES / 1024 / 1024,
  messageMax: CHAT_MESSAGE_MAX,
};

export function useValidationMessage() {
  const t = useTranslations('validation');

  return (key: string | undefined): string | undefined => {
    if (!key) return undefined;
    const validationKey = key as ValidationKey;
    if (t.has(validationKey)) return t(validationKey, VALIDATION_LIMITS);
    // A zod built-in message (not a messageKey) would leak English into any locale.
    if (process.env.NODE_ENV !== 'production') {
      console.warn('Untranslated validation message:', key);
    }
    return t('invalid');
  };
}
