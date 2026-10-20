import { z } from 'zod';

import { CHAT_MESSAGE_MAX } from './rules';

export const chatMessageSchema = z
  .string()
  .trim()
  .min(1, 'Message cannot be empty.')
  .max(
    CHAT_MESSAGE_MAX,
    `Message must be ${CHAT_MESSAGE_MAX} characters or fewer.`,
  );
