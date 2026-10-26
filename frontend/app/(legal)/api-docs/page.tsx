import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import CodeBlock from '../../components/CodeBlock';
import DocumentCard from '../../components/DocumentCard';
import LegalSection from '../../components/LegalSection';
import EndpointSection from './EndpointSection';
import { ENDPOINTS, ERROR_EXAMPLE } from './endpoints';

const OVERVIEW = [
  { id: 'baseUrl', code: 'https://localhost:8443' },
  { id: 'auth', code: 'Authorization: Bearer <accessToken>' },
  { id: 'rateLimit' },
  { id: 'errors', code: ERROR_EXAMPLE },
] as const;

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('apiDocs');
  return { title: t('title') };
}

export default async function ApiDocs() {
  const t = await getTranslations('apiDocs');

  return (
    <DocumentCard title={t('title')}>
      <p className="mb-5">{t('intro')}</p>

      {OVERVIEW.map((section) => (
        <LegalSection
          key={section.id}
          title={t(`overview.${section.id}.title`)}
        >
          {/* Static, never-reordered list: the index is a stable key. */}
          {(t.raw(`overview.${section.id}.paragraphs`) as string[]).map(
            (paragraph, paragraphIndex) => (
              <p key={paragraphIndex}>{paragraph}</p>
            ),
          )}
          {'code' in section && <CodeBlock>{section.code}</CodeBlock>}
        </LegalSection>
      ))}

      <LegalSection title={t('overview.interactive.title')}>
        <p>
          {t.rich('overview.interactive.body', {
            link: (chunks) => (
              // eslint-disable-next-line @next/next/no-html-link-for-pages -- /api/docs is the backend's Swagger UI (proxied by nginx), not a Next.js page
              <a
                href="/api/docs"
                className="text-on-elevated-surface-accent font-bold underline underline-offset-2"
              >
                {chunks}
              </a>
            ),
          })}
        </p>
      </LegalSection>

      {ENDPOINTS.map((endpoint) => (
        <EndpointSection key={endpoint.id} endpoint={endpoint} />
      ))}
    </DocumentCard>
  );
}
