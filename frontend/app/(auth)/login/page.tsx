'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import FormField from '../../components/FormField';
import { login } from '../../lib/login';
import { loginSchema, type LoginValues } from '../../lib/validation/auth';

export default function LoginPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loginSchema),
    mode: 'onTouched',
    defaultValues: { email: '', password: '' },
  });

  async function handleLogin({ email, password }: LoginValues) {
    const response = await login(email, password);
    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    // `assign`, not an `href` write: this handler now goes through
    // `handleSubmit`, which is called during render, so the compiler cannot
    // prove it is event-only and rejects writes to globals in it.
    window.location.assign(`/${response.message}`);
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

        <form onSubmit={handleSubmit(handleLogin)}>
          <FormField
            id="user-name"
            type="text"
            {...register('email')}
            error={errors.email?.message}
            placeholder="Email"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-password"
            type="password"
            {...register('password')}
            error={errors.password?.message}
            placeholder="Password"
            tone="card"
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
            terms checkbox in register/page.tsx keeps its named colours (12.5).
            Step 5 kept the technique and only changed what is named: the alpha
            is now taken off `on-card`, since `white` was an assumption
            about the card rather than a fact about it. */}
        <p className="text-on-card/50 mt-4 text-center text-xs">
          Don&apos;t have an account?{' '}
          <AccentLink href="/register">Register</AccentLink>
        </p>
      </Card>
    </div>
  );
}
