import type { Messages } from '../i18n/messages';

export type ValidationKey = keyof Messages['validation'];

// zod stores a translation key, not text: schemas are built once at module
// load, before any request knows its language. The UI translates at render.
export function messageKey(key: ValidationKey): ValidationKey {
  return key;
}
