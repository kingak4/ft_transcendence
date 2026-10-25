'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useForm } from 'react-hook-form';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import FormField from '../../components/FormField';
import { useValidationMessage } from '../../hooks/useValidationMessage';
import { register } from '../../lib/register';
import { registerSchema, type RegisterValues } from '../../lib/validation/auth';

export default function RegisterPage() {
  const t = useTranslations('auth');
  const tErrors = useTranslations('errors');
  const validationMessage = useValidationMessage();
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
      alert(response.message || tErrors('unknown'));
      return;
    }
    alert(t('register.success'));
    window.location.assign('/login');
  }

  return (
    <div className="flex flex-1 items-center justify-center">
      <Card>
        <h1 className="mb-1 text-xl font-extrabold">{t('register.title')}</h1>
        <p className="mb-6 text-sm font-medium">{t('register.subtitle')}</p>

        <form onSubmit={handleSubmit(handleRegister)}>
          <FormField
            id="user-name"
            type="text"
            {...registerField('email')}
            error={validationMessage(errors.email?.message)}
            label={t('email')}
            autoComplete="email"
            dir="ltr"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-password"
            type="password"
            {...registerField('password', { deps: ['confirmPassword'] })}
            error={validationMessage(errors.password?.message)}
            label={t('password')}
            autoComplete="new-password"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-confirm-password"
            type="password"
            {...registerField('confirmPassword')}
            error={validationMessage(errors.confirmPassword?.message)}
            label={t('confirmPassword')}
            autoComplete="new-password"
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
              <span className="text-on-card text-xs">
                {t.rich('register.agreeToTerms', {
                  terms: (chunks) => (
                    <Link
                      href="/terms-of-service"
                      className="text-on-card hover:no-underline underline transition-colors"
                    >
                      {chunks}
                    </Link>
                  ),
                  privacy: (chunks) => (
                    <Link
                      href="/privacy-policy"
                      className="text-on-card hover:no-underline underline transition-colors"
                    >
                      {chunks}
                    </Link>
                  ),
                })}
              </span>
            </label>
            {errors.agreedToTerms && (
              <p
                id="agreed-to-terms-error"
                className="text-danger mt-1 text-sm"
              >
                {validationMessage(errors.agreedToTerms.message)}
              </p>
            )}
          </div>

          <Button type="submit" fullWidth>
            {t('register.submit')}
          </Button>
        </form>

        <p className="text-on-card mt-4 text-center text-xs">
          {t.rich('register.haveAccount', {
            link: (chunks) => <AccentLink href="/login">{chunks}</AccentLink>,
          })}
        </p>
      </Card>
    </div>
  );
}
