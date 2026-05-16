"use client";

import Link from "next/link";
import React, { useState } from 'react';
import { login } from "../lib/login";

export default function SearchInput() {
  const [loginValue, setLogin] = useState("");
  const [passwordValue, setPassword] = useState("");

  return (
    <div className='flex flex-col w-screen min-h-screen justify-center border items-center'>
      <h1 className='font-bold text-3xl'>LOGIN</h1>
      <div className='flex flex-col w-1/6 min-h-3/4 border p-2 rounded-md justify-center items-center'>
        <div className="flex flex-row gap-2 justify-center">
          <input
            id="user-name"
            type="text"
            value={loginValue}
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
        <button onClick={() => loginWrap(loginValue, passwordValue)} className='border p-4 rounded-md cursor-pointer'>Login</button>
      </div>
    </div>
  );

  async function loginWrap(name: string, password: string) {
    const response = await login(loginValue, passwordValue)

    if (!response.success) {
      if (response.status === 500) {
        alert("Server error. Please try again later.");
      } else {
        alert(response.message || "An unknown error occurred.");
      }
      return;
    }
    alert("Succesfully logged in!");
    window.location.href = "/home";
  }
}