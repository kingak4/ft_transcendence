import ContactBlock from './ContactBlock';
import LegalSection from './LegalSection';

export interface LegalSectionContent {
  title: string;
  paragraphs: string[];
  items?: string[];
  contact?: boolean;
}

interface Props {
  title: string;
  lastUpdated?: string;
  sections: LegalSectionContent[];
}

export default function LegalDocument({ title, lastUpdated, sections }: Props) {
  return (
    <div className="bg-elevated-surface text-on-elevated-surface-body mx-auto flex max-w-[720px] flex-col gap-1.5 rounded-3xl p-6 text-base font-medium leading-relaxed shadow-[0_8px_24px_var(--color-shadow-card)] lg:px-14 lg:py-12">
      <h1 className="text-on-elevated-surface text-2xl font-extrabold tracking-tight lg:text-3xl">
        {title}
      </h1>

      {lastUpdated && (
        <p className="text-muted mb-5 text-sm font-semibold">{lastUpdated}</p>
      )}

      {sections.map((section) => (
        <LegalSection key={section.title} title={section.title}>
          {section.paragraphs.map((paragraph) => (
            <p key={paragraph}>{paragraph}</p>
          ))}

          {section.items && (
            <ul className="mt-1 list-disc space-y-2 ps-5">
              {section.items.map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          )}

          {section.contact && <ContactBlock />}
        </LegalSection>
      ))}
    </div>
  );
}
