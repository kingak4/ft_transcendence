'use client';


import React, { useState } from 'react';
import { login } from '../../lib/login';

export default function SearchInput() {
  const [loginValue, setLogin] = useState('');
  const [passwordValue, setPassword] = useState('');

  return (
    <div className="flex min-h-screen w-screen flex-col items-center justify-center border">
      <h1 className="text-3xl font-bold">LOGIN</h1>
      <h1 className="text-3xl font-bold">LOGIN</h1>
      <div className="min-h-3/4 flex w-1/6 flex-col items-center justify-center rounded-md border p-2">
        <div className="flex flex-row justify-center gap-2">
          <input
            id="user-name"
            type="text"
            value={loginValue}
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
          onClick={() => loginWrap()}
          className="cursor-pointer rounded-md border p-4"
        >
          Login
        </button>
      </div>
    </div>
  );

  async function loginWrap() {
    const response = await login(loginValue, passwordValue);

    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    alert('Succesfully logged in!');
    window.location.href = '/home';
  }
}
