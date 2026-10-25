import Link from 'next/link';
import { useTranslations } from 'next-intl';

import Icon from './Icon';

export default function BackButton() {
  const t = useTranslations('common');

  return (
    <Link
      href="/"
      className="text-on-surface inline-flex items-center gap-1 self-start text-sm font-semibold hover:underline"
    >
      <Icon name="arrow-left" size="sm" mirrorInRtl />
      {t('back')}
    </Link>
  );
}
