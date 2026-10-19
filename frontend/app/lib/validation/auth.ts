import { z } from 'zod';

import type { components } from '../../types/api';
import type { Satisfies } from './contract';
import { NO_WHITESPACE_PATTERN, PASSWORD_MAX, PASSWORD_MIN } from './rules';

type Schemas = components['schemas'];

// Messages are the backend's own (RegisterUseCase.java:15-17), so a user sees
// the same words whichever side catches the mistake.
//
// `.pipe` runs the email format check only once the first two pass, so an
// empty field says "blank" rather than "not valid". z.email() is stricter than
// Hibernate's @Email, which accepts `user@localhost` - the safe direction, as
// the browser never sends what the server would refuse.
const emailField = z
  .string()
  .min(1, 'Email cannot be blank')
  .regex(NO_WHITESPACE_PATTERN, 'Email cannot contain spaces')
  .pipe(z.email('Email should be valid'));

// Login checks presence only. Applying the registration rules here would lock
// out any account whose password predates them.
export const loginSchema = z.object({
  email: emailField,
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
    // A boolean refined to true rather than z.literal(true), so the form can
    // start unticked with a `false` default.
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
