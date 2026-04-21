"use client";

import Link from "next/link";
import React, { useState } from 'react';

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
        <button onClick={() => alert(`login=${loginValue}, password=${passwordValue}`)} className='border p-4 rounded-md cursor-pointer'>Login</button>
      </div>
    </div>
  );
}