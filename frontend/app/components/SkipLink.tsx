import { useTranslations } from 'next-intl';

export default function SkipLink() {
  const t = useTranslations('shell');

  return (
    <a
      href="#main"
      className="bg-elevated-surface text-on-elevated-surface sr-only rounded-xl px-4 py-2.5 text-sm font-bold shadow-[0_8px_24px_var(--color-shadow-card)] focus:not-sr-only focus:fixed focus:start-4 focus:top-4 focus:z-[60]"
    >
      {t('skipToMain')}
    </a>
  );
}
