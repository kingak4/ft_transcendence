'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useForm } from 'react-hook-form';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import FormField from '../../components/FormField';
import { register } from '../../lib/register';
import { registerSchema, type RegisterValues } from '../../lib/validation/auth';

export default function RegisterPage() {
  const {
    register: registerField,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(registerSchema),
    mode: 'onTouched',
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
      agreedToTerms: false,
    },
  });

  async function handleRegister({ email, password }: RegisterValues) {
    const response = await register(email, password);
    if (!response.success) {
      if (response.status === 500) {
        alert('Server error. Please try again later.');
      } else {
        alert(response.message || 'An unknown error occurred.');
      }
      return;
    }
    alert(`Successfully registered!\n${response.message}`);
    window.location.assign('/login');
  }

  return (
    <div className="flex flex-1 items-center justify-center">
      <Card>
        <h1 className="mb-1 text-xl font-extrabold">Register</h1>
        <p className="mb-6 text-sm font-medium opacity-60">Nice to meet you!</p>

        <form onSubmit={handleSubmit(handleRegister)}>
          <FormField
            id="user-name"
            type="text"
            {...registerField('email')}
            error={errors.email?.message}
            placeholder="Email"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-password"
            type="password"
            {...registerField('password', { deps: ['confirmPassword'] })}
            error={errors.password?.message}
            placeholder="Password"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-confirm-password"
            type="password"
            {...registerField('confirmPassword')}
            error={errors.confirmPassword?.message}
            placeholder="Confirm password"
            tone="card"
            className="mb-5"
          />

          <div className="mb-6">
            <label className="flex cursor-pointer items-start gap-2">
              <input
                type="checkbox"
                {...registerField('agreedToTerms')}
                aria-invalid={errors.agreedToTerms ? true : undefined}
                aria-describedby={
                  errors.agreedToTerms ? 'agreed-to-terms-error' : undefined
                }
                className="accent-primary mt-0.5"
              />
              <span className="text-on-card/60 text-xs">
                I agree to the{' '}
                <Link
                  href="/terms-of-service"
                  className="text-on-card/80 hover:text-on-card underline transition-colors"
                >
                  Terms of Service
                </Link>{' '}
                and{' '}
                <Link
                  href="/privacy-policy"
                  className="text-on-card/80 hover:text-on-card underline transition-colors"
                >
                  Privacy Policy
                </Link>
              </span>
            </label>
            {errors.agreedToTerms && (
              <p
                id="agreed-to-terms-error"
                className="text-danger mt-1 text-sm"
              >
                {errors.agreedToTerms.message}
              </p>
            )}
          </div>

          <Button type="submit" fullWidth>
            Register
          </Button>
        </form>

        <p className="text-on-card/50 mt-4 text-center text-xs">
          Already have an account? <AccentLink href="/login">Login</AccentLink>
        </p>
      </Card>
    </div>
  );
}
