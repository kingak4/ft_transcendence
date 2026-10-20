import { z } from 'zod';

import type { components } from '../../types/api';
import type { Satisfies } from './contract';
import {
  AVATAR_MAX_BYTES,
  AVATAR_TYPE_PREFIX,
  DISPLAY_NAME_MAX,
  DISPLAY_NAME_MIN,
  DISPLAY_NAME_PATTERN,
} from './rules';

type Schemas = components['schemas'];

// Messages are the backend's own (UpdateDisplayNameUseCase.java:17-21). The
// trim was already done by hand before this schema existed; it now happens
// here, and the trimmed value is what gets submitted.
export const displayNameSchema = z.object({
  displayName: z
    .string()
    .trim()
    .min(1, 'DisplayName cannot be blank')
    .min(
      DISPLAY_NAME_MIN,
      `DisplayName must be between ${DISPLAY_NAME_MIN} and ${DISPLAY_NAME_MAX} characters`,
    )
    .max(
      DISPLAY_NAME_MAX,
      `DisplayName must be between ${DISPLAY_NAME_MIN} and ${DISPLAY_NAME_MAX} characters`,
    )
    .regex(
      DISPLAY_NAME_PATTERN,
      'DisplayName can only contain letters, numbers, underscores, or periods',
    ),
});

export const avatarFileSchema = z
  .instanceof(File)
  .refine(
    (file) => file.type.startsWith(AVATAR_TYPE_PREFIX),
    'Please choose an image file.',
  )
  .refine(
    (file) => file.size <= AVATAR_MAX_BYTES,
    `Image must be ${AVATAR_MAX_BYTES / 1024 / 1024} MB or smaller.`,
  );

export type DisplayNameValues = z.output<typeof displayNameSchema>;

export type UpdateDisplayNameRequestBody = Satisfies<
  DisplayNameValues,
  Required<Schemas['UpdateDisplayNameRequest']>
>;
