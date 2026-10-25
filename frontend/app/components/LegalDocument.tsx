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

      {/* Static, never-reordered lists: the index is a stable key; text may repeat. */}
      {sections.map((section, sectionIndex) => (
        <LegalSection key={sectionIndex} title={section.title}>
          {section.paragraphs.map((paragraph, paragraphIndex) => (
            <p key={paragraphIndex}>{paragraph}</p>
          ))}

          {section.items && (
            <ul className="mt-1 list-disc space-y-2 ps-5">
              {section.items.map((item, itemIndex) => (
                <li key={itemIndex}>{item}</li>
              ))}
            </ul>
          )}

          {section.contact && <ContactBlock />}
        </LegalSection>
      ))}
    </div>
  );
}
