'use client';

import Link from 'next/link';
import { useState } from 'react';
import { login } from '../../lib/login';

export default function LoginPage() {
  const [loginValue, setLogin] = useState('');
  const [passwordValue, setPassword] = useState('');

  async function handleLogin() {
    const response = await login(loginValue, passwordValue);
    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    window.location.href = `/${response.message}`;
  }

  return (
    <div className="bg-gradient-login-page flex min-h-screen items-center justify-center">
      <div className="bg-brand-reversed-main-color w-80 rounded-2xl p-8">
        <h1 className="text-brand-main-color mb-1 text-2xl font-bold">Login</h1>
        <p className="text-brand-main-color/60 mb-6 text-sm">Welcome back!</p>

        <input
          id="user-name"
          type="text"
          value={loginValue}
          onChange={(e) => setLogin(e.target.value)}
          placeholder="Username"
          className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-secondary-color mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
        />

        <input
          id="user-password"
          type="password"
          value={passwordValue}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-secondary-color mb-2 w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
        />

        <div className="mb-6 text-right">
          <span className="text-brand-main-color/50 cursor-not-allowed text-xs">
            Forgot password?
          </span>
        </div>

        <button
          onClick={handleLogin}
          className="bg-brand-secondary-color text-brand-additional-color-2 mb-4 w-full rounded-lg py-3 text-sm font-bold transition-colors hover:brightness-125"
        >
          Login
        </button>

        <p className="text-brand-main-color/50 text-center text-xs">
          Don&apos;t have an account?{' '}
          <Link
            href="/register"
            className="text-brand-additional-color hover:text-brand-secondary-color font-bold transition-colors"
          >
            Register
          </Link>
        </p>
      </div>
    </div>
  );
}
