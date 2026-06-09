'use client';


import React, { useState } from 'react';

import { register } from '../../lib/register';

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
          onClick={() => registerWrap(emailValue, passwordValue)}
          className="cursor-pointer rounded-md border p-4"
        >
          Register
        </button>
      </div>
    </div>
  );

  async function registerWrap(name: string, password: string) {
    const response = await register(name, password);

    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    alert(`Succesfully registered!\n${response.message}`);
    window.location.href = '/home';
  }


}
