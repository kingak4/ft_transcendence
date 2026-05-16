"use client";

import Link from "next/link";
import React, { useState } from 'react';
import { client } from "../lib/api-clients";

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

    const { data, error, response } = await client.POST("/users/register", {
      body: {
        email: name,
        password: password
      },
    });

    if (!response.ok && error) {
      const status = error.status;
      const email_msg = error.message?.["register.command.email"];
      const passwrd_msg = error.message?.["register.command.rawPassword"];
      const message = `${error.detail}: ${email_msg}\n ${passwrd_msg}`;
      if (status == 400) {
        window.alert(`${message}`);
      }
      else
        window.alert(`Result: error ${error.status}`);
    }
    else {
      window.alert(`Zarejestrowano pomślnie:\nUser ID ${data?.id}`);
      window.location.href = "/login";
    }
  }

  interface RegisterError {
    status: number,
    error: string,
    message: {
      "register.command.email"?: string;
      "register.command.rawPassword"?: string;
    }
  }

  interface CreateUserPayload {
    email: string,
    password: string
  }

  interface CreateUserResponse {
    id: string,
  }
}