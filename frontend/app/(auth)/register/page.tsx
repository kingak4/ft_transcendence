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
    window.location.href = '/home';
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-register-page">
      <div className="w-80 rounded-2xl bg-brand-reversed-main-color p-8">
        <h1 className="mb-1 text-2xl font-bold text-brand-main-color">Register</h1>
        <p className="mb-6 text-sm text-brand-main-color/60">Nice to meet you!</p>

        <input
          id="user-name"
          type="text"
          value={nameValue}
          onChange={(e) => setName(e.target.value)}
          placeholder="Username"
          className="mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm text-brand-main-color outline-none placeholder:text-brand-main-color/40 focus:ring-1 focus:ring-brand-accent-color"
        />

        <input
          id="user-password"
          type="password"
          value={passwordValue}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="mb-3 w-full rounded-lg bg-white/10 px-4 py-3 text-sm text-brand-main-color outline-none placeholder:text-brand-main-color/40 focus:ring-1 focus:ring-brand-accent-color"
        />

        <input
          id="user-confirm-password"
          type="password"
          value={confirmPasswordValue}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="Confirm password"
          className="mb-5 w-full rounded-lg bg-white/10 px-4 py-3 text-sm text-brand-main-color outline-none placeholder:text-brand-main-color/40 focus:ring-1 focus:ring-brand-accent-color"
        />

        <label className="mb-6 flex cursor-pointer items-start gap-2">
          <input
            type="checkbox"
            checked={agreedToTerms}
            onChange={(e) => setAgreedToTerms(e.target.checked)}
            className="mt-0.5 accent-brand-accent-color"
          />
          <span className="text-xs text-brand-main-color/60">
            I agree to the{' '}
            <Link
              href="/terms-of-service"
              className="text-brand-main-color/80 underline transition-colors hover:text-brand-main-color"
            >
              Terms of Service
            </Link>{' '}
            and{' '}
            <Link
              href="/privacy-policy"
              className="text-brand-main-color/80 underline transition-colors hover:text-brand-main-color"
            >
              Privacy Policy
            </Link>
          </span>
        </label>

        <button
          onClick={handleRegister}
          className="mb-4 w-full rounded-lg bg-brand-accent-color py-3 text-sm font-bold text-brand-reversed-main-color transition-colors hover:brightness-110"
        >
          Register
        </button>

        <p className="text-center text-xs text-brand-main-color/50">
          Already have an account?{' '}
          <Link
            href="/login"
            className="font-bold text-brand-additional-color transition-colors hover:text-brand-secondary-color"
          >
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}
