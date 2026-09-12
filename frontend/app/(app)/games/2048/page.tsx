'use client';

import { use2048Game } from './hooks/use2048Game';
import { Board } from './components/Board';
import { GameHeader } from './components/GameHeader';
import InviteModal from '../components/InviteModal';

export default function My2048Game() {
  const game = use2048Game();

  return (
    <div
      className="text-hub-on-surface flex min-h-[85vh] touch-none select-none flex-col items-center justify-center p-4"
      onPointerDown={game.handlePointerDown}
      onPointerUp={game.handlePointerUp}
      onPointerCancel={game.handlePointerCancel}
    >
      <GameHeader
        score={game.score}
        moves={game.moves}
        onReset={game.resetGame}
        onInvite={() => game.setIsInviteModalOpen(true)}
      />

      {game.mounted ? (
        <Board
          tiles={game.tiles}
          score={game.score}
          gameOver={game.gameOver}
          gameWon={game.gameWon}
          onReset={game.resetGame}
          onContinue={game.handleContinue}
          onShare={() => game.setIsInviteModalOpen(true)}
          keepPlaying={game.keepPlaying}
        />
      ) : (
        <div className="bg-hub-panel border-hub-border aspect-square w-full max-w-[380px] rounded-2xl border p-3 shadow-xl sm:max-w-[420px]">
          <div className="grid h-full w-full grid-cols-4 grid-rows-4 gap-3">
            {Array(16)
              .fill(0)
              .map((_, i) => (
                <div
                  key={i}
                  className="bg-hub-panel-sunken/90 border-hub-border/50 h-full w-full rounded-xl border"
                />
              ))}
          </div>
        </div>
      )}

      <InviteModal
        gameName="2048"
        isOpen={game.isInviteModalOpen}
        onClose={() => game.setIsInviteModalOpen(false)}
        myUserId={null}
        currentScore={game.score}
      />
    </div>
  );
}
