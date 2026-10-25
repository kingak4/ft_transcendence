import { useTranslations } from 'next-intl';

export default function Hero() {
  const t = useTranslations('home');

  return (
    <div className="mx-auto flex max-w-[720px] flex-col items-center gap-3.5 text-center">
      <h1 className="text-on-start-page max-w-[600px] text-3xl font-extrabold leading-[1.1] tracking-[-1px] lg:text-5xl">
        {t('heroTitle')}
      </h1>
      <p className="text-on-start-page max-w-[520px] text-xl font-bold leading-relaxed">
        {t('heroSubtitle')}
      </p>
    </div>
  );
}
