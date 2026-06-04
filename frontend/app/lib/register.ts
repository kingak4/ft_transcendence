'use server';

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
      console.error('Register Error:', response.body);
      return {
        success: false,
        status: error?.status,
        message: `${error?.detail}:\n\n${errAny?.properties['register.command.email']}'\n'${errAny?.properties['register.command.rawPassword']}`,
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

export type ActionResponse = {
  success: boolean;
  status?: number;
  message?: string;
};
