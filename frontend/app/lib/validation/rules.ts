// Every constant here mirrors a rule the backend enforces, and names the line
// it was copied from. When the backend changes, this is the one file the
// frontend follows it in. CHAT_MESSAGE_MAX is the exception, and says so.

// RegisterUseCase.java:17 - `@Pattern(regexp = "^\\S+$")` on the email, and
// PasswordConstraintValidator's `WhitespaceRule` on the password.
export const NO_WHITESPACE_PATTERN = /^\S+$/;

// PasswordConstraintValidator.java - `new LengthRule(10, 60)`. Its
// `DictionaryRule` is deliberately not mirrored: shipping the common-passwords
// list to every browser would be heavy and would publish the blocklist, so
// that check stays server-side and surfaces through the existing error path.
export const PASSWORD_MIN = 10;
export const PASSWORD_MAX = 60;

// UpdateDisplayNameUseCase.java:18-20 - `@Size(min = 3, max = 20)` and
// `@Pattern(regexp = "^[a-zA-Z0-9_.]+$")`.
export const DISPLAY_NAME_MIN = 3;
export const DISPLAY_NAME_MAX = 20;
export const DISPLAY_NAME_PATTERN = /^[a-zA-Z0-9_.]+$/;

// application.yml:31 - `max-file-size: 10MB`. Spring's DataSize reads MB as
// 1024 * 1024 bytes, not 1000 * 1000.
export const AVATAR_MAX_BYTES = 10 * 1024 * 1024;

// No backend counterpart: the upload endpoint never checks the content type,
// so this prefix is the only type guard the avatar has.
export const AVATAR_TYPE_PREFIX = 'image/';

// FRONTEND-ONLY. The backend sets no length on messages - no annotation and no
// column length - so the server will accept longer ones. Add the same limit
// there before relying on this as a guarantee.
export const CHAT_MESSAGE_MAX = 500;
