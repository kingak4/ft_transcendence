'use client';

import {
  useState,
  useEffect,
  useCallback,
  useRef,
  useSyncExternalStore,
} from 'react';
import {
  Tile,
  Direction,
  initializeBoard,
  calcMove,
  createNewTile,
  checkGameOver,
  checkWin,
} from '../logic/gameLogic';

const emptySubscribe = () => () => {};

export function use2048Game() {
  const mounted = useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false,
  );
  const [tiles, setTiles] = useState<Tile[]>(() => initializeBoard());
  const [score, setScore] = useState(0);
  const [moves, setMoves] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [gameWon, setGameWon] = useState(false);
  const [keepPlaying, setKeepPlaying] = useState(false);
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);

  const isMovingRef = useRef(false);
  const pointerStartRef = useRef<{ x: number; y: number } | null>(null);

  const resetGame = useCallback(() => {
    setTiles(initializeBoard());
    setScore(0);
    setMoves(0);
    setGameOver(false);
    setGameWon(false);
    setKeepPlaying(false);
    isMovingRef.current = false;
  }, []);

  const handleContinue = useCallback(() => {
    setKeepPlaying(true);
  }, []);

  const handleMove = useCallback(
    (dir: Direction) => {
      if (gameOver) return;
      if (gameWon && !keepPlaying) return;
      if (isMovingRef.current) return;

      const result = calcMove(tiles, dir);

      if (result.changed) {
        isMovingRef.current = true;
        setTiles(result.newTiles);
        setScore((s) => s + result.score);
        setMoves((m) => m + 1);

        setTimeout(() => {
          setTiles((currentTiles) => {
            const newTile = createNewTile(currentTiles);
            const updatedTiles = newTile
              ? [...currentTiles, newTile]
              : currentTiles;

            if (checkWin(updatedTiles) && !gameWon) {
              setGameWon(true);
            } else if (checkGameOver(updatedTiles)) {
              setGameOver(true);
            }

            return updatedTiles;
          });

          isMovingRef.current = false;
        }, 50);
      }
    },
    [tiles, gameOver, gameWon, keepPlaying],
  );

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (isInviteModalOpen) return;

      if (
        ['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Space'].includes(
          e.key,
        )
      ) {
        e.preventDefault();
      }

      if (e.key === 'ArrowUp' || e.key === 'w' || e.key === 'W')
        handleMove('UP');
      if (e.key === 'ArrowDown' || e.key === 's' || e.key === 'S')
        handleMove('DOWN');
      if (e.key === 'ArrowLeft' || e.key === 'a' || e.key === 'A')
        handleMove('LEFT');
      if (e.key === 'ArrowRight' || e.key === 'd' || e.key === 'D')
        handleMove('RIGHT');
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [handleMove, isInviteModalOpen]);

  const handlePointerDown = useCallback(
    (e: React.PointerEvent) => {
      if (isInviteModalOpen) return;
      if ((e.target as HTMLElement).closest('button')) return;

      pointerStartRef.current = {
        x: e.clientX,
        y: e.clientY,
      };
    },
    [isInviteModalOpen],
  );

  const handlePointerUp = useCallback(
    (e: React.PointerEvent) => {
      if (isInviteModalOpen || !pointerStartRef.current) return;

      const deltaX = e.clientX - pointerStartRef.current.x;
      const deltaY = e.clientY - pointerStartRef.current.y;
      const minSwipeDistance = 30;

      const absX = Math.abs(deltaX);
      const absY = Math.abs(deltaY);

      if (Math.max(absX, absY) > minSwipeDistance) {
        if (absX > absY) {
          handleMove(deltaX > 0 ? 'RIGHT' : 'LEFT');
        } else {
          handleMove(deltaY > 0 ? 'DOWN' : 'UP');
        }
      }

      pointerStartRef.current = null;
    },
    [handleMove, isInviteModalOpen],
  );

  const handlePointerCancel = useCallback(() => {
    pointerStartRef.current = null;
  }, []);

  return {
    tiles,
    score,
    moves,
    gameOver,
    gameWon,
    keepPlaying,
    mounted,
    isInviteModalOpen,
    setIsInviteModalOpen,
    resetGame,
    handleContinue,
    handlePointerDown,
    handlePointerUp,
    handlePointerCancel,
  };
}
