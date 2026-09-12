'use client';

import { Tile } from '../logic/gameLogic';
import { getTileStyle } from './tileStyles';
import styles from './TileView.module.css';

type TileViewProps = {
  tile: Tile;
};

export function TileView({ tile }: TileViewProps) {
  const { bg, text, shadow } = getTileStyle(tile.value);
  const isLargeNumber = tile.value >= 100;
  const isHugeNumber = tile.value >= 1000;

  return (
    <div
      className="pointer-events-none absolute left-0 top-0"
      style={{
        width: 'calc((100% - 3 * 12px) / 4)',
        height: 'calc((100% - 3 * 12px) / 4)',
        transform: `translate(calc(${tile.x} * (100% + 12px)), calc(${tile.y} * (100% + 12px)))`,
        zIndex: tile.toDestroy ? 5 : tile.isMerged ? 15 : 10,
        opacity: tile.toDestroy ? 0 : 1,
        transition: 'transform 100ms ease-in-out, opacity 80ms ease-out',
      }}
    >
      <div
        className={`flex h-full w-full select-none items-center justify-center rounded-xl font-extrabold ${bg} ${text} ${shadow || ''} ${
          tile.isNew ? styles.tilePop : tile.isMerged ? styles.tileMerge : ''
        } ${
          isHugeNumber
            ? 'text-xl sm:text-2xl'
            : isLargeNumber
              ? 'text-2xl sm:text-3xl'
              : 'text-3xl sm:text-4xl'
        }`}
      >
        {tile.value}
      </div>
    </div>
  );
}
