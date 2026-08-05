'use server';

import type { ActionResponse } from './action-response';
import { client } from './api-clients';

export async function register(
  name: string,
  password: string,
): Promise<ActionResponse> {
  try {
    const { data, error, response } = await client.POST('/users/register', {
      body: {
        email: name,
        password: password,
      },
    });

    if (!response.ok) {
      /* eslint-disable-next-line @typescript-eslint/no-explicit-any */
      const errAny = error as any;
      console.error('Register Error:', error);
      let message = error?.detail || 'An error occurred during registration.';
      if (errAny?.properties) {
        const emailError = errAny.properties['register.command.email'];
        const passwordError = errAny.properties['register.command.rawPassword'];
        const details = [emailError, passwordError].filter(Boolean).join('\n');
        if (details) {
          message += `\n\n${details}`;
        }
      }

      return {
        success: false,
        status: error?.status,
        message,
      };
    }
    return { success: true, status: 200, message: `user ID: ${data?.id}` };
  } catch (error: unknown) {
    console.error('Network or Unexpected Error:', error);
    return {
      success: false,
      status: 500,
      message: 'Server error.',
    };
  }
}
