'use client';

import { useState, useEffect, useCallback, TouchEvent } from 'react';
import Card from '../../../components/Card';
import Button from '../../../components/Button';
import AccentLink from '../../../components/AccentLink';
import {
  Tile,
  initializeBoard,
  moveTiles,
  addRandomTile,
  checkGameOver,
  checkWin,
} from '../../../../lib/game2048Logic';
import InviteModal from '../components/InviteModal';

export default function Game2048Page() {
  const [tiles, setTiles] = useState<Tile[]>([]);
  const [score, setScore] = useState(0);
  const [moves, setMoves] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [gameWon, setGameWon] = useState(false);
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);
  const [touchStart, setTouchStart] = useState<{ x: number; y: number } | null>(
    null
  );

  useEffect(() => {
    setTiles(initializeBoard());
  }, []);

  const handleMove = useCallback(
    (direction: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT') => {
      if (gameOver || tiles.length === 0) return;

      const result = moveTiles(tiles, direction);

      if (result.changed) {
        const newTiles = [...result.newTiles];
        addRandomTile(newTiles);
        setTiles(newTiles);
        setScore((s) => s + result.score);
        setMoves((m) => m + 1);

        if (checkWin(newTiles) && !gameWon) {
          setGameWon(true);
        } else if (checkGameOver(newTiles)) {
          setGameOver(true);
        }
      }
    },
    [tiles, gameOver, gameWon]
  );

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        ['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key)
      ) {
        e.preventDefault();
      }

      switch (e.key) {
        case 'ArrowUp':
          handleMove('UP');
          break;
        case 'ArrowDown':
          handleMove('DOWN');
          break;
        case 'ArrowLeft':
          handleMove('LEFT');
          break;
        case 'ArrowRight':
          handleMove('RIGHT');
          break;
      }
    };

    window.addEventListener('keydown', handleKeyDown, { passive: false });
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [handleMove]);

  const handleTouchStart = (e: TouchEvent) => {
    setTouchStart({ x: e.touches[0].clientX, y: e.touches[0].clientY });
  };

  const handleTouchEnd = (e: TouchEvent) => {
    if (!touchStart) return;
    const touchEndX = e.changedTouches[0].clientX;
    const touchEndY = e.changedTouches[0].clientY;

    const dx = touchEndX - touchStart.x;
    const dy = touchEndY - touchStart.y;
    const absDx = Math.abs(dx);
    const absDy = Math.abs(dy);

    if (Math.max(absDx, absDy) > 30) {
      if (absDx > absDy) {
        handleMove(dx > 0 ? 'RIGHT' : 'LEFT');
      } else {
        handleMove(dy > 0 ? 'DOWN' : 'UP');
      }
    }
    setTouchStart(null);
  };

  const resetGame = () => {
    setTiles(initializeBoard());
    setScore(0);
    setMoves(0);
    setGameOver(false);
    setGameWon(false);
  };

  const getTileColor = (val: number) => {
    switch (val) {
      case 2:
        return 'bg-gray-200 text-gray-800';
      case 4:
        return 'bg-gray-300 text-gray-800';
      case 8:
        return 'bg-orange-200 text-orange-900';
      case 16:
        return 'bg-orange-300 text-orange-900';
      case 32:
        return 'bg-orange-400 text-white';
      case 64:
        return 'bg-orange-500 text-white';
      case 128:
        return 'bg-yellow-200 text-yellow-900';
      case 256:
        return 'bg-yellow-300 text-yellow-900';
      case 512:
        return 'bg-yellow-400 text-white';
      case 1024:
        return 'bg-yellow-500 text-white shadow-[0_0_10px_rgba(234,179,8,0.5)]';
      case 2048:
        return 'bg-yellow-600 text-white shadow-[0_0_15px_rgba(202,138,4,0.8)]';
      default:
        return 'bg-hub-border/20 text-transparent';
    }
  };

  return (
    <div className="flex flex-1 flex-col items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <Card>
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h1 className="mb-1 text-3xl font-extrabold">2048</h1>
              <AccentLink href="/games">← Back to Hub</AccentLink>
            </div>
            <div className="flex gap-2">
              <div className="min-w-[70px] rounded-md bg-black/10 p-2 text-center dark:bg-white/10">
                <p className="text-xs font-bold uppercase opacity-60">Moves</p>
                <p className="text-lg font-bold">{moves}</p>
              </div>
              <div className="min-w-[70px] rounded-md bg-black/10 p-2 text-center dark:bg-white/10">
                <p className="text-xs font-bold uppercase opacity-60">Score</p>
                <p className="text-lg font-bold">{score}</p>
              </div>
            </div>
          </div>

          <div
            className="relative mb-6 mx-auto aspect-square w-full touch-none rounded-lg bg-black/10 dark:bg-white/10 p-2"
            onTouchStart={handleTouchStart}
            onTouchEnd={handleTouchEnd}
          >
            {gameOver && (
              <div className="absolute inset-0 z-20 flex flex-col items-center justify-center rounded-lg bg-black/80 backdrop-blur-sm">
                <h2 className="mb-4 text-3xl font-bold text-white">
                  Game Over!
                </h2>
                <Button onClick={resetGame}>Try Again</Button>
              </div>
            )}
            {gameWon && (
              <div className="absolute inset-0 z-20 flex flex-col items-center justify-center rounded-lg bg-yellow-600/90 backdrop-blur-sm">
                <h2 className="mb-4 text-3xl font-bold text-white">You Win!</h2>
                <Button onClick={resetGame}>Play Again</Button>
              </div>
            )}

            {/* Background Grid */}
            <div className="absolute inset-0 z-0 grid grid-cols-4 grid-rows-4 gap-2 p-2">
              {Array(16)
                .fill(0)
                .map((_, i) => (
                  <div
                    key={i}
                    className="rounded-md bg-black/5 dark:bg-white/5"
                  ></div>
                ))}
            </div>

            {/* Foreground Animated Tiles */}
            <div className="absolute inset-0 z-10 p-2">
              {tiles.map((tile) => (
                <div
                  key={tile.id}
                  className="absolute transition-all duration-150 ease-[cubic-bezier(0.77,0.09,0.15,0.91)]"
                  style={{
                    width: 'calc(25% - 6px)',
                    height: 'calc(25% - 6px)',
                    top: `calc(${tile.r * 25}% + ${tile.r * 2}px)`,
                    left: `calc(${tile.c * 25}% + ${tile.c * 2}px)`,
                    zIndex: tile.toDestroy ? 0 : 10,
                    opacity: tile.toDestroy ? 0 : 1,
                  }}
                >
                  <div
                    className={`flex h-full w-full items-center justify-center rounded-md text-xl font-bold transition-transform duration-200 md:text-2xl scale-100 ${getTileColor(tile.value)}`}
                  >
                    {tile.value !== 0 ? tile.value : ''}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="flex gap-2">
            <Button onClick={resetGame} fullWidth variant="secondary">
              New Game
            </Button>
            <Button onClick={() => setIsInviteModalOpen(true)} fullWidth variant="primary">
              Zaproś 💬
            </Button>
          </div>
        </Card>
      </div>

      <InviteModal
        gameName="2048"
        isOpen={isInviteModalOpen}
        onClose={() => setIsInviteModalOpen(false)}
        myUserId={null}
      />
    </div>
  );
}
