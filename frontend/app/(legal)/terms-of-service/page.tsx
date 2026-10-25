import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import LegalDocument, {
  type LegalSectionContent,
} from '../../components/LegalDocument';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('legal.terms');
  return { title: t('title') };
}

export default async function TermsOfService() {
  const t = await getTranslations('legal.terms');

  return (
    <LegalDocument
      title={t('title')}
      lastUpdated={t('lastUpdated')}
      sections={t.raw('sections') as LegalSectionContent[]}
    />
  );
}
