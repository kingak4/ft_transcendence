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

export function apiErrorKey(
  context: ApiErrorContext,
  status: number | undefined,
): ErrorKey {
  if (status === undefined) return 'unknown';
  if (status >= 500) return 'server';
  return STATUS_KEYS[context][status] ?? 'unknown';
}
