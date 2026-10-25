'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslations } from 'next-intl';
import { useForm } from 'react-hook-form';

import AccentLink from '../../components/AccentLink';
import Button from '../../components/Button';
import Card from '../../components/Card';
import FormField from '../../components/FormField';
import { useValidationMessage } from '../../hooks/useValidationMessage';
import { login } from '../../lib/login';
import { loginSchema, type LoginValues } from '../../lib/validation/auth';

export default function LoginPage() {
  const t = useTranslations('auth');
  const tErrors = useTranslations('errors');
  const validationMessage = useValidationMessage();
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
      alert(response.message || tErrors('unknown'));
      return;
    }
    window.location.assign(`/${response.message}`);
  }

  return (
    <div className="flex flex-1 items-center justify-center">
      <Card>
        <h1 className="mb-1 text-xl font-extrabold">{t('login.title')}</h1>
        <p className="mb-6 text-sm font-medium">{t('login.subtitle')}</p>

        <form onSubmit={handleSubmit(handleLogin)}>
          <FormField
            id="user-name"
            type="text"
            {...register('email')}
            error={validationMessage(errors.email?.message)}
            label={t('email')}
            autoComplete="email"
            inputMode="email"
            dir="ltr"
            tone="card"
            className="mb-3"
          />

          <FormField
            id="user-password"
            type="password"
            {...register('password')}
            error={validationMessage(errors.password?.message)}
            label={t('password')}
            autoComplete="current-password"
            tone="card"
            className="mb-6"
          />

          <Button type="submit" fullWidth>
            {t('login.submit')}
          </Button>
        </form>

        <p className="text-on-card mt-4 text-center text-xs">
          {t.rich('login.noAccount', {
            link: (chunks) => (
              <AccentLink href="/register">{chunks}</AccentLink>
            ),
          })}
        </p>
      </Card>
    </div>
  );
}
