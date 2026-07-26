'use client';

import Link from 'next/link';
import { useState, type FormEvent } from 'react';

import Button from '../../components/Button';
import Card from '../../components/Card';
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
        <h1 className="mb-1 text-2xl font-bold">Register</h1>
        <p className="text-on-inverse-surface/60 mb-6 text-sm">
          Nice to meet you!
        </p>

        <form onSubmit={handleRegister}>
          <input
            id="user-name"
            type="text"
            value={nameValue}
            onChange={(e) => setName(e.target.value)}
            placeholder="Username"
            className="text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary mb-3 w-full rounded-lg bg-on-inverse-surface/10 px-4 py-3 text-sm outline-none focus:ring-1"
          />

          <input
            id="user-password"
            type="password"
            value={passwordValue}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            className="text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary mb-3 w-full rounded-lg bg-on-inverse-surface/10 px-4 py-3 text-sm outline-none focus:ring-1"
          />

          <input
            id="user-confirm-password"
            type="password"
            value={confirmPasswordValue}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm password"
            className="text-on-inverse-surface placeholder:text-on-inverse-surface/40 focus:ring-primary mb-5 w-full rounded-lg bg-on-inverse-surface/10 px-4 py-3 text-sm outline-none focus:ring-1"
          />

          <label className="mb-6 flex cursor-pointer items-start gap-2">
            <input
              type="checkbox"
              checked={agreedToTerms}
              onChange={(e) => setAgreedToTerms(e.target.checked)}
              className="accent-primary mt-0.5"
            />
            <span className="text-on-inverse-surface/60 text-xs">
              I agree to the{' '}
              <Link
                href="/terms-of-service"
                className="text-on-inverse-surface/80 hover:text-on-inverse-surface underline transition-colors"
              >
                Terms of Service
              </Link>{' '}
              and{' '}
              <Link
                href="/privacy-policy"
                className="text-on-inverse-surface/80 hover:text-on-inverse-surface underline transition-colors"
              >
                Privacy Policy
              </Link>
            </span>
          </label>

          <Button type="submit">Register</Button>
        </form>

        <p className="text-on-inverse-surface/50 mt-4 text-center text-xs">
          Already have an account?{' '}
          <Link
            href="/login"
            className="text-primary font-bold transition-colors hover:brightness-125"
          >
            Login
          </Link>
        </p>
      </Card>
    </div>
  );
}
