"use server";

import { cookies } from 'next/headers';
import { postData } from './post';

export async function login(name: string, password: string): Promise<ActionResponse> {
  const payload: CreateUserPayload = {
    email: name,
    password: password
  }

  const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';

  try {

    const result = await postData<CreateUserResponse, CreateUserPayload>(
      backendUrl + "/users/login",
      payload
    );
    const token = result.accessToken;

    const cookieStore = await cookies();
    cookieStore.set('auth_token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production', // Use HTTPS in prod
      sameSite: 'lax',
      path: '/',
    });
    return { success: true, status: 200 };
  }
  catch (error: any) {
    console.error("Login Error:", error.message);

    return {
      success: false,
      status: error.status || 500,
      message: error.message
    };
  }
}

export interface CreateUserPayload {
  email: string,
  password: string
}

export interface CreateUserResponse {
  accessToken: string,
  tokenType: string
}

export type ActionResponse = {
  success: boolean;
  status?: number;
  message?: string;
};