import { useTranslations } from 'next-intl';

import AccentLink from '../../components/AccentLink';
import Card from '../../components/Card';

export default function GamesHubPage() {
  const t = useTranslations('games');

  return (
    <div className="flex flex-1 flex-col items-center justify-center p-4">
      <Card>
        <h1 className="mb-1 text-2xl font-extrabold">{t('hub.title')}</h1>
        <p className="mb-6 text-sm font-medium opacity-60">
          {t('hub.subtitle')}
        </p>

        <div className="flex flex-col gap-4">
          <div className="border-elevated-border/50 hover:border-elevated-border rounded-lg border p-4 transition-colors">
            <h2 className="mb-1 text-lg font-bold">{t('game2048.title')}</h2>
            <p className="mb-3 text-sm opacity-60">
              {t('game2048.description')}
            </p>
            <AccentLink href="/games/2048">{t('game2048.play')}</AccentLink>
          </div>
        </div>
      </Card>
    </div>
  );
}
