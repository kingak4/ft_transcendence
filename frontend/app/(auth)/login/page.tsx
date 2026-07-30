'use client';

import { useState, type FormEvent } from 'react';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import TextField from '../../components/TextField';
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
        <p className="text-on-elevated-surface/60 mb-6 text-sm">
          Welcome back!
        </p>

        <form onSubmit={handleLogin}>
          <TextField
            id="user-name"
            type="text"
            value={loginValue}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Username"
            tone="elevated"
            className="mb-3"
          />

          <TextField
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            tone="elevated"
            className="mb-2"
          />

          <div className="mb-6 text-right">
            <span className="text-on-elevated-surface/50 cursor-not-allowed text-xs">
              Forgot password?
            </span>
          </div>

          <Button type="submit" fullWidth>
            Login
          </Button>
        </form>

        <p className="text-on-elevated-surface/50 mt-4 text-center text-xs">
          Don&apos;t have an account?{' '}
          <AccentLink href="/register">Register</AccentLink>
        </p>
      </Card>
    </div>
  );
}
