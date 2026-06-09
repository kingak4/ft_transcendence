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
    window.location.href = '/home';
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-login-page">
      <div className="w-80 rounded-2xl bg-brand-reversed-main-color p-8">
        <h1 className="mb-1 text-2xl font-bold text-brand-main-color">Login</h1>
        <p className="mb-6 text-sm text-brand-main-color/60">Welcome back!</p>

        <input
          id="user-name"
          type="text"
          value={loginValue}
          onChange={(e) => setLogin(e.target.value)}
          placeholder="Username"
          className="mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm text-brand-main-color outline-none placeholder:text-brand-main-color/40 focus:ring-1 focus:ring-brand-secondary-color"
        />

        <input
          id="user-password"
          type="password"
          value={passwordValue}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="mb-2 w-full rounded-lg bg-white/10 px-4 py-3 text-sm text-brand-main-color outline-none placeholder:text-brand-main-color/40 focus:ring-1 focus:ring-brand-secondary-color"
        />

        <div className="mb-6 text-right">
          <span className="cursor-not-allowed text-xs text-brand-main-color/50">
            Forgot password?
          </span>
        </div>

        <button
          onClick={handleLogin}
          className="mb-4 w-full rounded-lg bg-brand-secondary-color py-3 text-sm font-bold text-brand-additional-color-2 transition-colors hover:brightness-125"
        >
          Login
        </button>

        <p className="text-center text-xs text-brand-main-color/50">
          Don&apos;t have an account?{' '}
          <Link
            href="/register"
            className="font-bold text-brand-additional-color transition-colors hover:text-brand-secondary-color"
          >
            Register
          </Link>
        </p>
      </div>
    </div>
  );
}
