"use client";

import Link from "next/link";
import React, { useState } from 'react';

export default function SearchInput() {
  const [emailValue, setLogin] = useState("");
  const [passwordValue, setPassword] = useState("");

  return (
    <div className='flex flex-col w-screen min-h-screen justify-center border items-center'>
      <h1 className='font-bold text-3xl'>REGISTER</h1>
      <div className='flex flex-col w-1/6 min-h-3/4 border p-2 rounded-md justify-center items-center'>
        <div className="flex flex-row gap-2 justify-center">
          <input
            id="user-name"
            type="text"
            value={emailValue}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Login"
            className="border p-2 rounded-md w-3/4"
          />
        </div>
        <div className='flex flex-row items-center gap-2 justify-center'>
          <input
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password..."
            className="border p-2 rounded-md w-3/4"
          />
        </div>
        <button onClick={() => register(emailValue, passwordValue)} className='border p-4 rounded-md cursor-pointer'>Register</button>
      </div>
    </div>
  );

  async function register(name: string, password: string) {
    const payload: CreateUserPayload = {
      email: name,
      password: password
    }

    const result = await postData<CreateUserResponse, CreateUserPayload>(
      '/api/users/register',
      payload
    );

    console.log(`Result: token=${result.id}`);
  }

  interface CreateUserPayload {
    email: string,
    password: string
  }

  interface CreateUserResponse {
    id: string,
  }

  async function postData<TResponse, TBody>(url: string, body: TBody): Promise<TResponse> {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok)
      throw new Error(`HTTP error! status: ${response.status}`);

    return await response.json() as TResponse;
  }
}