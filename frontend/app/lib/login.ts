'use server';

import { cookies } from 'next/headers';
// import { postData } from './post';
import { client } from './api-clients';

export async function login(
  name: string,
  password: string,
): Promise<ActionResponse> {
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
        message: error?.detail,
      };
    }

    const token = data?.accessToken;
    const id = data?.userId;

    if (!token) {
      return {
        success: false,
        message: 'Recieved empty token from server.',
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
      message: 'Server error.',
    };
  }
}

export interface loginError {
  status: number;
  error: string;
  message: string;
}

export interface CreateUserPayload {
  email: string;
  password: string;
}

export interface CreateUserResponse {
  accessToken: string;
  tokenType: string;
}

export type ActionResponse = {
  success: boolean;
  status?: number;
  message?: string;
};
