'use client';

import Link from 'next/link';
import { useState, type FormEvent } from 'react';

import Button from '../../components/Button';
import Card from '../../components/Card';
import { login } from '../../lib/login';

export default function LoginPage() {
  const [loginValue, setLogin] = useState('');
  const [passwordValue, setPassword] = useState('');

  async function handleLogin(e: FormEvent) {
    e.preventDefault();
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
    <div className="flex flex-1 items-center justify-center">
      <Card>
        <h1 className="mb-1 text-2xl font-bold">Login</h1>
        <p className="text-on-inverse-surface/60 mb-6 text-sm">Welcome back!</p>

        <form onSubmit={handleLogin}>
          <input
            id="user-name"
            type="text"
            value={loginValue}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Username"
            className="text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary mb-3 w-full rounded-lg bg-on-inverse-surface/10 px-4 py-3 text-sm outline-none focus:ring-1"
          />

          <input
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            className="text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary mb-2 w-full rounded-lg bg-on-inverse-surface/10 px-4 py-3 text-sm outline-none focus:ring-1"
          />

          <div className="mb-6 text-right">
            <span className="text-on-inverse-surface/50 cursor-not-allowed text-xs">
              Forgot password?
            </span>
          </div>

          <Button type="submit">Login</Button>
        </form>

        <p className="text-on-inverse-surface/50 mt-4 text-center text-xs">
          Don&apos;t have an account?{' '}
          <Link
            href="/register"
            className="text-primary font-bold transition-colors hover:brightness-125"
          >
            Register
          </Link>
        </p>
      </Card>
    </div>
  );
}
