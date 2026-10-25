import { z } from 'zod';

import type { components } from '../../types/api';
import type { Satisfies } from './contract';
import { messageKey } from './messages';
import {
  AVATAR_MAX_BYTES,
  AVATAR_TYPE_PREFIX,
  DISPLAY_NAME_MAX,
  DISPLAY_NAME_MIN,
  DISPLAY_NAME_PATTERN,
} from './rules';

type Schemas = components['schemas'];

export const displayNameSchema = z.object({
  displayName: z
    .string()
    .trim()
    .min(1, messageKey('displayNameBlank'))
    .min(DISPLAY_NAME_MIN, messageKey('displayNameLength'))
    .max(DISPLAY_NAME_MAX, messageKey('displayNameLength'))
    .regex(DISPLAY_NAME_PATTERN, messageKey('displayNamePattern')),
});

export const avatarFileSchema = z
  .instanceof(File)
  .refine(
    (file) => file.type.startsWith(AVATAR_TYPE_PREFIX),
    messageKey('avatarType'),
  )
  .refine(
    (file) => file.size <= AVATAR_MAX_BYTES,
    messageKey('avatarSize'),
  );

export type DisplayNameValues = z.output<typeof displayNameSchema>;

export type UpdateDisplayNameRequestBody = Satisfies<
  DisplayNameValues,
  Required<Schemas['UpdateDisplayNameRequest']>
>;
