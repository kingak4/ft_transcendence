import { useTranslations } from 'next-intl';

import Button from './Button';
import Card from './Card';

type SessionCardProps = {
  title: string;
  subtitle: string;
  userId: string;
  logoutAction: () => Promise<void>;
  headingLevel?: 'h1' | 'h2';
};

export default function SessionCard({
  title,
  subtitle,
  userId,
  logoutAction,
  headingLevel: Heading = 'h2',
}: SessionCardProps) {
  const t = useTranslations('session');

  return (
    <Card>
      <Heading className="mb-1 text-xl font-extrabold">{title}</Heading>
      <p className="mb-6 text-sm font-medium opacity-60">{subtitle}</p>
      <div className="flex flex-col gap-4">
        <Button href={`/${userId}`} fullWidth>
          {t('goToProfile')}
        </Button>
        <form action={logoutAction}>
          <Button type="submit" variant="outline" fullWidth>
            {t('logOut')}
          </Button>
        </form>
      </div>
    </Card>
  );
}
