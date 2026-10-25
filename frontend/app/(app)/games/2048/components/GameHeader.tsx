'use client';

type GameHeaderProps = {
  score: number;
  moves: number;
  onReset: () => void;
  onInvite: () => void;
};

export function GameHeader({
  score,
  moves,
  onReset,
  onInvite,
}: GameHeaderProps) {
  return (
    <div className="mb-4 w-full max-w-[380px] sm:max-w-[420px]">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h1 className="text-on-elevated-surface text-5xl font-black tracking-tight">
            2048
          </h1>
          <p className="text-muted mt-0.5 text-xs font-semibold">
            Join the tiles and reach 2048!
          </p>
        </div>
        <div className="flex gap-2">
          <div className="bg-elevated-surface border-elevated-border min-w-[70px] rounded-xl border px-3.5 py-2 text-center shadow-sm">
            <span className="text-muted block text-[10px] font-bold uppercase leading-tight">
              Moves
            </span>
            <span className="text-on-elevated-surface text-base font-extrabold">
              {moves}
            </span>
          </div>
          <div className="bg-elevated-surface border-elevated-border min-w-[70px] rounded-xl border px-3.5 py-2 text-center shadow-sm">
            <span className="text-muted block text-[10px] font-bold uppercase leading-tight">
              Score
            </span>
            <span className="text-accent-teal text-base font-extrabold">
              {score}
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={onReset}
          className="bg-cta text-on-primary flex-1 cursor-pointer rounded-xl py-2 text-center text-sm font-bold shadow-sm transition-transform hover:opacity-90 active:scale-95"
        >
          New Game
        </button>
        <button
          onClick={onInvite}
          className="bg-elevated-surface hover:bg-field text-on-elevated-surface border-elevated-border flex-1 cursor-pointer rounded-xl border py-2 text-center text-sm font-bold shadow-sm transition-transform active:scale-95"
        >
          Challenge a friend
        </button>
      </div>
    </div>
  );
}
