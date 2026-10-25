import { useTranslations } from 'next-intl';

const CONTACT_EMAIL = 'kinga.kwasniak5@gmail.com';

// A postal address stays as written on the envelope, in every language.
const ADDRESS_LINES = [
  'ft_transcendence',
  '42 Warsaw',
  'Al. Solidarności 171B',
  '00-877 Warszawa',
];

export default function ContactBlock({
  className = '',
}: {
  className?: string;
}) {
  const t = useTranslations('legal.contact');
  const lines = [
    ...ADDRESS_LINES,
    t('country'),
    t('email', { email: CONTACT_EMAIL }),
  ];

  return (
    <div className={`space-y-2.5 ${className}`}>
      {lines.map((line) => (
        <p key={line}>
          <bdi>{line}</bdi>
        </p>
      ))}
    </div>
  );
}
