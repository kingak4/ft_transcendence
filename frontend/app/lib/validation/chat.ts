import { z } from 'zod';

import { messageKey } from './messages';
import { CHAT_MESSAGE_MAX } from './rules';

export const chatMessageSchema = z
  .string()
  .trim()
  .min(1, messageKey('messageEmpty'))
  .max(CHAT_MESSAGE_MAX, messageKey('messageTooLong'));
