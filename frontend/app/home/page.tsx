"use client";

import Link from "next/link";
import React, { useState } from 'react';

export default function SearchInput() {
  const [emailValue, setLogin] = useState("");
  const [passwordValue, setPassword] = useState("");

  return (
    <div className='flex flex-col w-screen min-h-screen justify-center border items-center'>
      <h1 className='font-bold text-3xl'>HOME</h1>
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