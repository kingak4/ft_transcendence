import ContactBlock from './ContactBlock';
import DocumentCard from './DocumentCard';
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
    <DocumentCard title={title}>
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
    </DocumentCard>
  );
}
