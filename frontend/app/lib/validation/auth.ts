import { z } from 'zod';

import type { components } from '../../types/api';
import type { Satisfies } from './contract';
import { NO_WHITESPACE_PATTERN, PASSWORD_MAX, PASSWORD_MIN } from './rules';

type Schemas = components['schemas'];

const emailField = z
  .string()
  .min(1, 'Email cannot be blank')
  .regex(NO_WHITESPACE_PATTERN, 'Email cannot contain spaces')
  .pipe(z.email('Email should be valid'));

export const loginSchema = z.object({
  email: z.string().min(1, 'Email cannot be blank'),
  password: z.string().min(1, 'Password cannot be blank'),
});

export const registerSchema = z
  .object({
    email: emailField,
    password: z
      .string()
      .min(1, 'Password cannot be blank')
      .min(
        PASSWORD_MIN,
        `Password must be between ${PASSWORD_MIN} and ${PASSWORD_MAX} characters`,
      )
      .max(
        PASSWORD_MAX,
        `Password must be between ${PASSWORD_MIN} and ${PASSWORD_MAX} characters`,
      )
      .regex(NO_WHITESPACE_PATTERN, 'Password cannot contain whitespace'),
    confirmPassword: z.string(),
    agreedToTerms: z
      .boolean()
      .refine(Boolean, 'Please agree to the Terms & Privacy Policy.'),
  })
  .refine((values) => values.password === values.confirmPassword, {
    message: 'Passwords do not match.',
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
