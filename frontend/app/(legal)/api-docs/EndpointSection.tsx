import { useTranslations } from 'next-intl';

import CodeBlock from '../../components/CodeBlock';
import LegalSection from '../../components/LegalSection';
import Tag from '../../components/Tag';
import type { ENDPOINTS, EndpointId, ParamName } from './endpoints';

type DocumentedEndpoint = (typeof ENDPOINTS)[number];
type ApiDocsT = ReturnType<typeof useTranslations<'apiDocs'>>;
type ApiDocsKey = Parameters<ApiDocsT>[0];

// Each slot only accepts its own endpoints.<id>.params.<name> key, and only
// if that message exists, so an override can't point at missing text.
type ParamOverrides = {
  [Id in EndpointId]?: {
    [Name in ParamName]?: Extract<`endpoints.${Id}.params.${Name}`, ApiDocsKey>;
  };
};

const PARAM_OVERRIDES: ParamOverrides = {
  addFriend: { friendId: 'endpoints.addFriend.params.friendId' },
  removeFriend: { friendId: 'endpoints.removeFriend.params.friendId' },
};

// Endpoint-specific text wins; the shared params.<name> text is the default.
function paramDescription(
  t: ApiDocsT,
  endpointId: EndpointId,
  name: ParamName,
) {
  return t(PARAM_OVERRIDES[endpointId]?.[name] ?? `params.${name}`);
}

const SUBHEADING_CLASSES =
  'text-on-elevated-surface mt-2 text-sm font-extrabold';
const CELL_CLASSES = 'border-elevated-border border-b py-2 pe-4 align-top';

export default function EndpointSection({
  endpoint,
}: {
  endpoint: DocumentedEndpoint;
}) {
  const t = useTranslations('apiDocs');
  const paramsHeadingId = `${endpoint.id}-params`;

  return (
    <LegalSection title={t(`endpoints.${endpoint.id}.summary`)}>
      <p className="flex flex-wrap items-center gap-3">
        <Tag>{endpoint.method}</Tag>
        <code
          dir="ltr"
          className="text-on-elevated-surface break-all font-mono text-sm font-bold"
        >
          {endpoint.path}
        </code>
      </p>

      <p>{t(`endpoints.${endpoint.id}.description`)}</p>

      {endpoint.params.length > 0 && (
        <>
          <h3 id={paramsHeadingId} className={SUBHEADING_CLASSES}>
            {t('labels.parameters')}
          </h3>
          <div
            tabIndex={0}
            role="region"
            aria-labelledby={paramsHeadingId}
            className="overflow-x-auto"
          >
            <table className="w-full text-start text-sm">
              <thead>
                <tr className="text-on-elevated-surface">
                  <th scope="col" className={`${CELL_CLASSES} text-start`}>
                    {t('table.name')}
                  </th>
                  <th scope="col" className={`${CELL_CLASSES} text-start`}>
                    {t('table.location')}
                  </th>
                  <th scope="col" className={`${CELL_CLASSES} text-start`}>
                    {t('table.type')}
                  </th>
                  <th scope="col" className={`${CELL_CLASSES} text-start`}>
                    {t('table.required')}
                  </th>
                  <th scope="col" className={`${CELL_CLASSES} text-start`}>
                    {t('table.description')}
                  </th>
                </tr>
              </thead>
              <tbody>
                {endpoint.params.map((param) => (
                  <tr key={param.name}>
                    <td className={CELL_CLASSES}>
                      <code dir="ltr" className="font-mono font-bold">
                        {param.name}
                      </code>
                    </td>
                    <td className={CELL_CLASSES}>
                      <code dir="ltr" className="font-mono">
                        {param.location}
                      </code>
                    </td>
                    <td className={CELL_CLASSES}>
                      <code dir="ltr" className="font-mono">
                        {param.type}
                      </code>
                    </td>
                    <td className={CELL_CLASSES}>
                      {param.required ? t('table.yes') : t('table.no')}
                    </td>
                    <td className={CELL_CLASSES}>
                      {paramDescription(t, endpoint.id, param.name)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {endpoint.requestExample && (
        <>
          <h3 className={SUBHEADING_CLASSES}>{t('labels.requestExample')}</h3>
          <CodeBlock>{endpoint.requestExample}</CodeBlock>
        </>
      )}

      {endpoint.responseExample && (
        <>
          <h3 className={SUBHEADING_CLASSES}>{t('labels.responseExample')}</h3>
          <CodeBlock>{endpoint.responseExample}</CodeBlock>
        </>
      )}

      <h3 className={SUBHEADING_CLASSES}>{t('labels.responses')}</h3>
      <ul className="space-y-1">
        {endpoint.statuses.map((status) => (
          <li key={status} className="flex gap-3">
            <code
              dir="ltr"
              className="text-on-elevated-surface font-mono font-bold"
            >
              {status}
            </code>
            <span>{t(`status.${status}`)}</span>
          </li>
        ))}
      </ul>
    </LegalSection>
  );
}
