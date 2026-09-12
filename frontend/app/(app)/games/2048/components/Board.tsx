'use client';

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
  return (
    <div className="bg-hub-panel-sunken border-hub-border relative aspect-square w-full max-w-[380px] select-none rounded-2xl border p-3 shadow-2xl sm:max-w-[420px]">
      <div className="grid h-full w-full grid-cols-4 grid-rows-4 gap-3">
        {Array(16)
          .fill(0)
          .map((_, i) => (
            <div
              key={i}
              className="bg-hub-field/60 border-hub-border/40 h-full w-full rounded-xl border shadow-inner"
            />
          ))}
      </div>

      <div className="pointer-events-none absolute inset-3">
        {tiles.map((tile) => (
          <TileView key={tile.id} tile={tile} />
        ))}
      </div>

      {gameOver && (
        <div className="bg-hub-panel/90 border-hub-border animate-in fade-in absolute inset-0 z-30 flex flex-col items-center justify-center rounded-2xl border p-4 backdrop-blur-md duration-300">
          <h2 className="mb-1 text-4xl font-black text-red-500">Game Over!</h2>
          <p className="text-hub-muted mb-2 text-xs font-semibold">
            No moves left
          </p>
          <div className="bg-hub-panel-sunken border-hub-border mb-5 rounded-xl border px-4 py-1.5 text-center">
            <span className="text-hub-muted block text-[10px] font-bold uppercase">
              Final Score
            </span>
            <span className="text-hub-teal text-xl font-extrabold">
              {score}
            </span>
          </div>
          <div className="flex w-full max-w-[280px] gap-2.5">
            <button
              onClick={onReset}
              className="bg-hub-panel hover:bg-hub-field text-hub-on-surface border-hub-border flex-1 cursor-pointer rounded-xl border py-2.5 text-xs font-bold shadow-md transition-transform active:scale-95"
            >
              Try Again
            </button>
            <button
              onClick={onShare}
              className="bg-hub-cta text-hub-ink flex-1 cursor-pointer rounded-xl py-2.5 text-xs font-bold shadow-md transition-transform hover:opacity-90 active:scale-95"
            >
              Share Score
            </button>
          </div>
        </div>
      )}

      {gameWon && !keepPlaying && (
        <div className="bg-hub-panel/95 border-hub-border animate-in fade-in absolute inset-0 z-30 flex flex-col items-center justify-center rounded-2xl border p-4 backdrop-blur-md duration-300">
          <h2 className="text-hub-teal mb-1 text-4xl font-black drop-shadow-md">
            You Win!
          </h2>
          <p className="text-hub-muted mb-2 text-xs font-semibold">
            You reached the 2048 tile!
          </p>
          <div className="bg-hub-panel-sunken border-hub-border mb-5 rounded-xl border px-4 py-1.5 text-center">
            <span className="text-hub-muted block text-[10px] font-bold uppercase">
              Current Score
            </span>
            <span className="text-hub-teal text-xl font-extrabold">
              {score}
            </span>
          </div>
          <div className="flex w-full max-w-[280px] flex-col gap-2">
            <div className="flex w-full gap-2">
              <button
                onClick={onContinue}
                className="bg-hub-cta text-hub-ink flex-1 cursor-pointer rounded-xl py-2.5 text-xs font-bold shadow-md transition-transform hover:opacity-90 active:scale-95"
              >
                Keep Playing
              </button>
              <button
                onClick={onShare}
                className="bg-hub-panel hover:bg-hub-field text-hub-on-surface border-hub-border flex-1 cursor-pointer rounded-xl border py-2.5 text-xs font-bold shadow-md transition-transform active:scale-95"
              >
                Share Score
              </button>
            </div>
            <button
              onClick={onReset}
              className="hover:bg-hub-field/50 text-hub-muted w-full cursor-pointer rounded-lg bg-transparent py-2 text-xs font-semibold transition-colors"
            >
              New Game
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
