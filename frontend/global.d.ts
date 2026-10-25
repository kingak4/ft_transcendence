import type { Locale } from './app/lib/i18n/config';
import type { Messages } from './app/lib/i18n/messages';

// Types every t('…') key and useLocale() against the English messages.
declare module 'next-intl' {
  interface AppConfig {
    Locale: Locale;
    Messages: Messages;
  }
}
