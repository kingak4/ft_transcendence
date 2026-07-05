'use client';

import Link from 'next/link';
import { useState } from 'react';
import { register } from '../../lib/register';

export default function RegisterPage() {
  const [nameValue, setName] = useState('');
  const [passwordValue, setPassword] = useState('');
  const [confirmPasswordValue, setConfirmPassword] = useState('');
  const [agreedToTerms, setAgreedToTerms] = useState(false);

  async function handleRegister() {
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
    <div className="bg-gradient-register-page flex min-h-screen items-center justify-center">
      <div className="bg-brand-reversed-main-color w-80 rounded-2xl p-8">
        <h1 className="text-brand-main-color mb-1 text-2xl font-bold">
          Register
        </h1>
        <p className="text-brand-main-color/60 mb-6 text-sm">
          Nice to meet you!
        </p>

        <input
          id="user-name"
          type="text"
          value={nameValue}
          onChange={(e) => setName(e.target.value)}
          placeholder="Username"
          className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-accent-color mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
        />

        <input
          id="user-password"
          type="password"
          value={passwordValue}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-accent-color mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
        />

        <input
          id="user-confirm-password"
          type="password"
          value={confirmPasswordValue}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="Confirm password"
          className="text-brand-main-color placeholder:text-brand-main-color/40 focus:ring-brand-accent-color mb-5 w-full rounded-lg bg-white/10 px-4 py-3 text-sm outline-none focus:ring-1"
        />

        <label className="mb-6 flex cursor-pointer items-start gap-2">
          <input
            type="checkbox"
            checked={agreedToTerms}
            onChange={(e) => setAgreedToTerms(e.target.checked)}
            className="accent-brand-accent-color mt-0.5"
          />
          <span className="text-brand-main-color/60 text-xs">
            I agree to the{' '}
            <Link
              href="/terms-of-service"
              className="text-brand-main-color/80 hover:text-brand-main-color underline transition-colors"
            >
              Terms of Service
            </Link>{' '}
            and{' '}
            <Link
              href="/privacy-policy"
              className="text-brand-main-color/80 hover:text-brand-main-color underline transition-colors"
            >
              Privacy Policy
            </Link>
          </span>
        </label>

        <button
          onClick={handleRegister}
          className="bg-brand-accent-color text-brand-reversed-main-color mb-4 w-full rounded-lg py-3 text-sm font-bold transition-colors hover:brightness-110"
        >
          Register
        </button>

        <p className="text-brand-main-color/50 text-center text-xs">
          Already have an account?{' '}
          <Link
            href="/login"
            className="text-brand-additional-color hover:text-brand-secondary-color font-bold transition-colors"
          >
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}
