'use client';

import Link from 'next/link';
import React, { useState } from 'react';
import { client } from '../lib/api-clients';

export default function SearchInput() {
  const [emailValue, setLogin] = useState('');
  const [passwordValue, setPassword] = useState('');

  return (
    <div className="flex min-h-screen w-screen flex-col items-center justify-center border">
      <h1 className="text-3xl font-bold">REGISTER</h1>
      <div className="min-h-3/4 flex w-1/6 flex-col items-center justify-center rounded-md border p-2">
        <div className="flex flex-row justify-center gap-2">
          <input
            id="user-name"
            type="text"
            value={emailValue}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Login"
            className="w-3/4 rounded-md border p-2"
          />
        </div>
        <div className="flex flex-row items-center justify-center gap-2">
          <input
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password..."
            className="w-3/4 rounded-md border p-2"
          />
        </div>
        <button
          onClick={() => register(emailValue, passwordValue)}
          className="cursor-pointer rounded-md border p-4"
        >
          Register
        </button>
      </div>
    </div>
  );

  async function register(name: string, password: string) {
    const payload: CreateUserPayload = {
      email: name,
      password: password,
    };

    const { data, error, response } = await client.POST('/users/register', {
      body: {
        email: name,
        password: password,
      },
    });

    if (!response.ok) {
      const serverError = error as unknown as RegisterError;
      const status = serverError.status;
      const message =
        serverError.properties['register.command.email'] +
        '\n' +
        serverError.properties['register.command.rawPassword'];
      if (status == 400) {
        window.alert(`${message}`);
      } else window.alert(`Result: ID=${data?.id}`);
    } else {
      window.alert(
        `Zarejestrowano pomślnie:\nlogin: ${name}\npassword: ${password}`,
      );
      window.location.href = '/login';
    }
  }

  interface RegisterError {
    status: number;
    error: string;
    properties: {
      'register.command.email'?: string;
      'register.command.rawPassword'?: string;
    };
  }

  interface CreateUserPayload {
    email: string;
    password: string;
  }

  interface CreateUserResponse {
    id: string;
  }

  // async function postData<TResponse, TBody>(url: string, body: TBody): Promise<TResponse> {
  //   const response = await fetch(url, {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json',
  //     },
  //     body: JSON.stringify(body),
  //   });

  //   if (!response.ok)
  //     throw new Error(`HTTP error! status: ${response.status}`);

  //   return await response.json() as TResponse;
  // }
}
