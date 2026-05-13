"use server";

import { cookies } from 'next/headers';
// import { postData } from './post';
import { client } from './api-clients';
import { errorToJSON } from 'next/dist/server/render';

export async function login(name: string, password: string): Promise<ActionResponse> {
  const payload: CreateUserPayload = {
    email: name,
    password: password
  }

  // const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';

  try {

    const { data, error, response } = await client.POST("/users/login", {
      body: {
        email: name,
        password: password
      },
    });

    if (!response.ok) {
      console.error("Login Error:", response.body);
      const serverError = (error as unknown) as loginError;
      return {
        success: false,
        status: serverError.status,
        message: serverError.message
      };
    }

    const token = data?.accessToken;

    if (!token) {
      return {
        success: false,
        message: "Otrzymano pusty token z serwera."
      };
    }

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
    console.error("Network or Unexpected Error:", error);
    return {
      success: false,
      status: 500,
      message: "Problem z połączeniem lub błąd wewnętrzny aplikacji."
    };
  }
}

export interface loginError {
  "status": number,
  "error": string,
  "message": string
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