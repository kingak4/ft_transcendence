import { z } from 'zod';

import type { components } from '../../types/api';
import type { Satisfies } from './contract';
import { messageKey } from './messages';
import { NO_WHITESPACE_PATTERN, PASSWORD_MAX, PASSWORD_MIN } from './rules';

type Schemas = components['schemas'];

const emailField = z
  .string()
  .min(1, messageKey('emailBlank'))
  .regex(NO_WHITESPACE_PATTERN, messageKey('emailSpaces'))
  .pipe(z.email(messageKey('emailInvalid')));

export const loginSchema = z.object({
  email: emailField,
  password: z.string().min(1, messageKey('passwordBlank')),
});

export const registerSchema = z
  .object({
    email: emailField,
    password: z
      .string()
      .min(1, messageKey('passwordBlank'))
      .min(PASSWORD_MIN, messageKey('passwordLength'))
      .max(PASSWORD_MAX, messageKey('passwordLength'))
      .regex(NO_WHITESPACE_PATTERN, messageKey('passwordWhitespace')),
    confirmPassword: z.string(),
    agreedToTerms: z.boolean().refine(Boolean, messageKey('termsRequired')),
  })
  .refine((values) => values.password === values.confirmPassword, {
    message: messageKey('passwordsMismatch'),
    path: ['confirmPassword'],
  });

export type LoginValues = z.output<typeof loginSchema>;
export type RegisterValues = z.output<typeof registerSchema>;

export type LoginRequestBody = Satisfies<
  LoginValues,
  Required<Schemas['LoginRequest']>
>;
export type RegisterRequestBody = Satisfies<
  Pick<RegisterValues, 'email' | 'password'>,
  Required<Schemas['RegisterRequest']>
>;
