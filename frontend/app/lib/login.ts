'use server';

import { getTranslations } from 'next-intl/server';
import { cookies } from 'next/headers';
import type { ActionResponse } from './action-response';
import { client } from './api-clients';
import { apiErrorKey } from './i18n/apiErrors';

export async function login(
  name: string,
  password: string,
): Promise<ActionResponse> {
  const t = await getTranslations('errors');

  try {
    const { data, error, response } = await client.POST('/users/login', {
      body: {
        email: name,
        password: password,
      },
    });

    if (!response.ok) {
      console.error('Login Error:', error);
      return {
        success: false,
        status: error?.status,
        message: t(apiErrorKey('login', response.status)),
      };
    }

    const token = data?.accessToken;
    const id = data?.userId;

    if (!token) {
      console.error('Login Error: received empty token from server.');
      return {
        success: false,
        message: t('unknown'),
      };
    }

    const cookieStore = await cookies();
    cookieStore.set('auth_token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      path: '/',
    });

    if (id) {
      cookieStore.set('user_id', id, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        path: '/',
      });
    }

    return { success: true, status: 200, message: id };
  } catch (error: unknown) {
    console.error('Network or Unexpected Error:', error);
    return {
      success: false,
      status: 500,
      message: t('server'),
    };
  }
}
