const CONTACT_LINES = [
  'ft_transcendence',
  '42 Warsaw',
  'Al. Solidarności 171B',
  '00-877 Warszawa',
  'Poland',
  'Email: kinga.kwasniak5@gmail.com',
];

export default function ContactBlock({
  className = '',
}: {
  className?: string;
}) {
  return (
    <div className={`space-y-2.5 ${className}`}>
      {CONTACT_LINES.map((line) => (
        <p key={line}>{line}</p>
      ))}
    </div>
  );
}
