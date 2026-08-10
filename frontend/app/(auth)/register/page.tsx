'use client';

import Link from 'next/link';
import { useState, type FormEvent } from 'react';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import TextField from '../../components/TextField';
import { register } from '../../lib/register';

export default function RegisterPage() {
  const [nameValue, setName] = useState('');
  const [passwordValue, setPassword] = useState('');
  const [confirmPasswordValue, setConfirmPassword] = useState('');
  const [agreedToTerms, setAgreedToTerms] = useState(false);

  async function handleRegister(e: FormEvent) {
    e.preventDefault();
    if (passwordValue !== confirmPasswordValue) {
      alert('Passwords do not match.');
      return;
    }
    if (!agreedToTerms) {
      alert('Please agree to the Terms & Privacy Policy.');
      return;
    }
    const response = await register(nameValue, passwordValue);
    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    alert(`Successfully registered!\n${response.message}`);
    window.location.href = '/login';
  }

  return (
    <div className="flex flex-1 items-center justify-center">
      <Card>
        <h1 className="mb-1 text-xl font-extrabold">Register</h1>
        {/* Inherited and modulated rather than named - see login/page.tsx. Only
            the leaf text nodes convert: `opacity` compounds down the tree and
            colour alpha does not, so the two blocks below that wrap coloured
            children keep their named colours (12.5). */}
        <p className="mb-6 text-sm font-medium opacity-60">Nice to meet you!</p>

        <form onSubmit={handleRegister}>
          <TextField
            id="user-name"
            type="text"
            value={nameValue}
            onChange={(e) => setName(e.target.value)}
            placeholder="Email"
            tone="card"
            className="mb-3"
          />

          <TextField
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            tone="card"
            className="mb-3"
          />

          <TextField
            id="user-confirm-password"
            type="password"
            value={confirmPasswordValue}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm password"
            tone="card"
            className="mb-5"
          />

          <label className="mb-6 flex cursor-pointer items-start gap-2">
            <input
              type="checkbox"
              checked={agreedToTerms}
              onChange={(e) => setAgreedToTerms(e.target.checked)}
              className="accent-primary mt-0.5"
            />
            {/* Named alpha, deliberately. `opacity-60` here plus `opacity-80` on
                the links would multiply to 0.48 and render the links DIMMER
                than the sentence they sit in, inverting the emphasis - alpha is
                absolute, opacity compounds. Recorded in 12.5. */}
            <span className="text-hub-on-card/60 text-xs">
              I agree to the{' '}
              <Link
                href="/terms-of-service"
                className="text-hub-on-card/80 hover:text-hub-on-card underline transition-colors"
              >
                Terms of Service
              </Link>{' '}
              and{' '}
              <Link
                href="/privacy-policy"
                className="text-hub-on-card/80 hover:text-hub-on-card underline transition-colors"
              >
                Privacy Policy
              </Link>
            </span>
          </label>

          <Button type="submit" fullWidth>
            Register
          </Button>
        </form>

        {/* Named alpha for the same reason as the block above: this wraps an
            `AccentLink` with its own `text-primary`, which opacity would fade
            along with the sentence. */}
        <p className="text-hub-on-card/50 mt-4 text-center text-xs">
          Already have an account? <AccentLink href="/login">Login</AccentLink>
        </p>
      </Card>
    </div>
  );
}
