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
        <p className="mb-6 text-sm font-medium text-white/60">
          Nice to meet you!
        </p>

        <form onSubmit={handleRegister}>
          <TextField
            id="user-name"
            type="text"
            value={nameValue}
            onChange={(e) => setName(e.target.value)}
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
            className="mb-3"
          />

          <TextField
            id="user-confirm-password"
            type="password"
            value={confirmPasswordValue}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm password"
            tone="elevated"
            className="mb-5"
          />

          <label className="mb-6 flex cursor-pointer items-start gap-2">
            <input
              type="checkbox"
              checked={agreedToTerms}
              onChange={(e) => setAgreedToTerms(e.target.checked)}
              className="accent-primary mt-0.5"
            />
            <span className="text-xs text-white/60">
              I agree to the{' '}
              <Link
                href="/terms-of-service"
                className="text-white/80 underline transition-colors hover:text-white"
              >
                Terms of Service
              </Link>{' '}
              and{' '}
              <Link
                href="/privacy-policy"
                className="text-white/80 underline transition-colors hover:text-white"
              >
                Privacy Policy
              </Link>
            </span>
          </label>

          <Button type="submit" fullWidth>
            Register
          </Button>
        </form>

        <p className="mt-4 text-center text-xs text-white/50">
          Already have an account? <AccentLink href="/login">Login</AccentLink>
        </p>
      </Card>
    </div>
  );
}
