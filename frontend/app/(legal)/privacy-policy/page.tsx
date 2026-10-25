import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import LegalDocument, {
  type LegalSectionContent,
} from '../../components/LegalDocument';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('legal.privacy');
  return { title: t('title') };
}

export default async function PrivacyPolicy() {
  const t = await getTranslations('legal.privacy');

  return (
    <LegalDocument
      title={t('title')}
      sections={t.raw('sections') as LegalSectionContent[]}
    />
  );
}
