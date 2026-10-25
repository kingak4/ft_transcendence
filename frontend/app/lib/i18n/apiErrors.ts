import type { Messages } from './messages';

export type ErrorKey = keyof Messages['errors'];

type ApiErrorContext = 'login' | 'register';

// Backend `detail` text is English-only, so it is never shown; the status
// picks a translated message instead. Unlisted statuses fall back to `unknown`.
const STATUS_KEYS: Record<
  ApiErrorContext,
  Partial<Record<number, ErrorKey>>
> = {
  login: {
    400: 'invalidCredentials',
    401: 'invalidCredentials',
    404: 'invalidCredentials',
  },
  register: {
    400: 'registrationRejected',
    409: 'emailTaken',
  },
};

// A 400 lists failed fields by Bean Validation path (method.param.field) with
// English text, so the field - not the text - picks the message. A backend
// rename only degrades to the generic `registrationRejected`.
const REGISTER_FIELD_KEYS: Partial<Record<string, ErrorKey>> = {
  'register.command.email': 'emailRejected',
  'register.command.rawPassword': 'passwordRejected',
};

function failedFields(error: unknown): string[] {
  if (typeof error !== 'object' || error === null) return [];
  const { properties } = error as { properties?: unknown };
  if (typeof properties !== 'object' || properties === null) return [];
  return Object.keys(properties);
}

// Specific only when exactly one known field failed; otherwise undefined.
export function registerFieldErrorKey(error: unknown): ErrorKey | undefined {
  const keys = failedFields(error).map((field) => REGISTER_FIELD_KEYS[field]);
  return keys.length === 1 ? keys[0] : undefined;
}

export function apiErrorKey(
  context: ApiErrorContext,
  status: number | undefined,
): ErrorKey {
  if (status === undefined) return 'unknown';
  if (status >= 500) return 'server';
  return STATUS_KEYS[context][status] ?? 'unknown';
}
