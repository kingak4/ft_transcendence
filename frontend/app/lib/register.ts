'use server';

import { getTranslations } from 'next-intl/server';
import type { ActionResponse } from './action-response';
import { client } from './api-clients';
import { apiErrorKey } from './i18n/apiErrors';

export async function register(
  name: string,
  password: string,
): Promise<ActionResponse> {
  const t = await getTranslations('errors');

  try {
    const { data, error, response } = await client.POST('/users/register', {
      body: {
        email: name,
        password: password,
      },
    });

    if (!response.ok) {
      console.error('Register Error:', error);
      return {
        success: false,
        status: error?.status,
        message: t(apiErrorKey('register', response.status)),
      };
    }
    return { success: true, status: 200, message: `user ID: ${data?.id}` };
  } catch (error: unknown) {
    console.error('Network or Unexpected Error:', error);
    return {
      success: false,
      status: 500,
      message: t('server'),
    };
  }
}
