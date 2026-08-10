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
        <h1 className="mb-1 text-xl font-extrabold">Login</h1>
        {/* Colour is INHERITED and modulated, not named - the technique Footer
            uses, applied to Card's children. Card sets `text-white` on itself,
            and naming that same white again at reduced alpha declared the card's
            foreground in five places, four of which are not Card and would only
            be found by grep. Step 5 is still deciding whether these surfaces
            theme; if this card ever goes light, `opacity` follows it and
            `text-white/60` would be white on white. */}
        <p className="mb-6 text-sm font-medium opacity-60">Welcome back!</p>

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
            <span className="cursor-not-allowed text-xs opacity-50">
              Forgot password?
            </span>
          </div>

          <Button type="submit" fullWidth>
            Login
          </Button>
        </form>

        {/* Stays on named alpha, unlike the two above. This paragraph wraps an
            `AccentLink`, which sets its own `text-primary`: colour alpha is
            absolute and leaves that child alone, while `opacity` applies to the
            whole subtree and would fade the accent to half. Same reason the
            terms checkbox in register/page.tsx keeps its named colours (12.5). */}
        <p className="mt-4 text-center text-xs text-white/50">
          Don&apos;t have an account?{' '}
          <AccentLink href="/register">Register</AccentLink>
        </p>
      </Card>
    </div>
  );
}
