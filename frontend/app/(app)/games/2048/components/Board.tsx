'use client';

import { useTranslations } from 'next-intl';

import { Tile } from '../logic/gameLogic';
import { TileView } from './TileView';

type BoardProps = {
  tiles: Tile[];
  score: number;
  gameOver: boolean;
  gameWon: boolean;
  onReset: () => void;
  onContinue: () => void;
  onShare: () => void;
  keepPlaying: boolean;
};

export function Board({
  tiles,
  score,
  gameOver,
  gameWon,
  onReset,
  onContinue,
  onShare,
  keepPlaying,
}: BoardProps) {
  const t = useTranslations('games.game2048');

  return (
    <div className="bg-sunken-surface border-elevated-border relative aspect-square w-full max-w-[380px] select-none rounded-2xl border p-3 shadow-2xl sm:max-w-[420px]">
      <div className="grid h-full w-full grid-cols-4 grid-rows-4 gap-3">
        {Array(16)
          .fill(0)
          .map((_, i) => (
            <div
              key={i}
              className="bg-field/60 border-elevated-border/40 h-full w-full rounded-xl border shadow-inner"
            />
          ))}
      </div>

      <div className="pointer-events-none absolute inset-3">
        {tiles.map((tile) => (
          <TileView key={tile.id} tile={tile} />
        ))}
      </div>

      {gameOver && (
        <div className="bg-elevated-surface/90 border-elevated-border animate-in fade-in absolute inset-0 z-30 flex flex-col items-center justify-center rounded-2xl border p-4 backdrop-blur-md duration-300">
          <h2 className="text-danger mb-1 text-4xl font-black">
            {t('gameOver')}
          </h2>
          <p className="text-muted mb-2 text-xs font-semibold">
            {t('noMoves')}
          </p>
          <div className="bg-sunken-surface border-elevated-border mb-5 rounded-xl border px-4 py-1.5 text-center">
            <span className="text-muted block text-[10px] font-bold uppercase">
              {t('finalScore')}
            </span>
            <span className="text-accent-teal text-xl font-extrabold">
              {score}
            </span>
          </div>
          <div className="flex w-full max-w-[280px] gap-2.5">
            <button
              onClick={onReset}
              className="bg-elevated-surface hover:bg-field text-on-elevated-surface border-elevated-border flex-1 cursor-pointer rounded-xl border py-2.5 text-xs font-bold shadow-md transition-transform active:scale-95"
            >
              {t('tryAgain')}
            </button>
            <button
              onClick={onShare}
              className="bg-cta text-on-primary flex-1 cursor-pointer rounded-xl py-2.5 text-xs font-bold shadow-md transition-transform hover:opacity-90 active:scale-95"
            >
              {t('shareScore')}
            </button>
          </div>
        </div>
      )}

      {gameWon && !keepPlaying && (
        <div className="bg-elevated-surface/95 border-elevated-border animate-in fade-in absolute inset-0 z-30 flex flex-col items-center justify-center rounded-2xl border p-4 backdrop-blur-md duration-300">
          <h2 className="text-accent-teal mb-1 text-4xl font-black drop-shadow-md">
            {t('youWin')}
          </h2>
          <p className="text-muted mb-2 text-xs font-semibold">
            {t('reached2048')}
          </p>
          <div className="bg-sunken-surface border-elevated-border mb-5 rounded-xl border px-4 py-1.5 text-center">
            <span className="text-muted block text-[10px] font-bold uppercase">
              {t('currentScore')}
            </span>
            <span className="text-accent-teal text-xl font-extrabold">
              {score}
            </span>
          </div>
          <div className="flex w-full max-w-[280px] flex-col gap-2">
            <div className="flex w-full gap-2">
              <button
                onClick={onContinue}
                className="bg-cta text-on-primary flex-1 cursor-pointer rounded-xl py-2.5 text-xs font-bold shadow-md transition-transform hover:opacity-90 active:scale-95"
              >
                {t('keepPlaying')}
              </button>
              <button
                onClick={onShare}
                className="bg-elevated-surface hover:bg-field text-on-elevated-surface border-elevated-border flex-1 cursor-pointer rounded-xl border py-2.5 text-xs font-bold shadow-md transition-transform active:scale-95"
              >
                {t('shareScore')}
              </button>
            </div>
            <button
              onClick={onReset}
              className="hover:bg-field/50 text-muted w-full cursor-pointer rounded-lg bg-transparent py-2 text-xs font-semibold transition-colors"
            >
              {t('newGame')}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
