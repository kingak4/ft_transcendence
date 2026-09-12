export type Tile = {
  id: string;
  value: number;
  x: number;
  y: number;
  isNew?: boolean;
  isMerged?: boolean;
  toDestroy?: boolean;
};

export type Direction = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT';

export const VECTORS: Record<Direction, { dx: number; dy: number }> = {
  UP: { dx: 0, dy: -1 },
  DOWN: { dx: 0, dy: 1 },
  LEFT: { dx: -1, dy: 0 },
  RIGHT: { dx: 1, dy: 0 },
};

let tileIdCounter = 0;

export function getRandomEmptyPosition(
  tiles: Tile[],
): { x: number; y: number } | null {
  const emptyCells: { x: number; y: number }[] = [];
  for (let y = 0; y < 4; y++) {
    for (let x = 0; x < 4; x++) {
      if (!tiles.find((t) => t.x === x && t.y === y && !t.toDestroy)) {
        emptyCells.push({ x, y });
      }
    }
  }
  if (emptyCells.length === 0) return null;
  return emptyCells[Math.floor(Math.random() * emptyCells.length)];
}

export function createNewTile(tiles: Tile[]): Tile | null {
  const pos = getRandomEmptyPosition(tiles);
  if (!pos) return null;
  const rand = Math.random();
  return {
    id: 'tl' + String(tileIdCounter++),
    value: rand < 0.1 ? 4 : 2,
    x: pos.x,
    y: pos.y,
    isNew: true,
  };
}

export function initializeBoard(): Tile[] {
  tileIdCounter = 0;
  const board: Tile[] = [];

  for (let i = 0; i < 2; i++) {
    const tile = createNewTile(board);
    if (!tile) break;
    board.push(tile);
  }
  return board;
}

export function calcMove(
  tiles: Tile[],
  direction: Direction,
): { newTiles: Tile[]; changed: boolean; score: number } {
  const newTiles = tiles
    .filter((t) => !t.toDestroy)
    .map((t) => ({ ...t, isMerged: false, isNew: false }));

  const grid: (Tile | null)[][] = Array(4)
    .fill(null)
    .map(() => Array(4).fill(null));

  newTiles.forEach((t) => {
    grid[t.y][t.x] = t;
  });

  const vector = VECTORS[direction];
  const xTraverse = direction === 'RIGHT' ? [3, 2, 1, 0] : [0, 1, 2, 3];
  const yTraverse = direction === 'DOWN' ? [3, 2, 1, 0] : [0, 1, 2, 3];

  let changed = false;
  let score = 0;

  for (const y of yTraverse) {
    for (const x of xTraverse) {
      const tile = grid[y][x];
      if (!tile) continue;

      let currX = x;
      let currY = y;
      let nextX = currX + vector.dx;
      let nextY = currY + vector.dy;

      while (nextX >= 0 && nextX < 4 && nextY >= 0 && nextY < 4) {
        const nextTile = grid[nextY][nextX];

        if (nextTile === null) {
          currX = nextX;
          currY = nextY;
          nextX += vector.dx;
          nextY += vector.dy;
        } else if (
          nextTile.value === tile.value &&
          !nextTile.isMerged &&
          !nextTile.toDestroy
        ) {
          currX = nextX;
          currY = nextY;
          break;
        } else {
          break;
        }
      }

      if (currX !== x || currY !== y) {
        changed = true;
        const targetTile = grid[currY][currX];

        if (
          targetTile &&
          targetTile.value === tile.value &&
          !targetTile.isMerged &&
          !targetTile.toDestroy
        ) {
          targetTile.value *= 2;
          targetTile.isMerged = true;
          score += targetTile.value;

          tile.x = currX;
          tile.y = currY;
          tile.toDestroy = true;

          grid[y][x] = null;
        } else {
          grid[y][x] = null;
          grid[currY][currX] = tile;
          tile.x = currX;
          tile.y = currY;
        }
      }
    }
  }

  return { newTiles, changed, score };
}

export function checkGameOver(tiles: Tile[]): boolean {
  const activeTiles = tiles.filter((t) => !t.toDestroy);
  if (activeTiles.length < 16) return false;

  const grid: (Tile | null)[][] = Array(4)
    .fill(null)
    .map(() => Array(4).fill(null));

  activeTiles.forEach((t) => {
    grid[t.y][t.x] = t;
  });

  for (let y = 0; y < 4; y++) {
    for (let x = 0; x < 4; x++) {
      const val = grid[y][x]?.value;
      if (!val) return false;
      if (x < 3 && grid[y][x + 1]?.value === val) return false;
      if (y < 3 && grid[y + 1][x]?.value === val) return false;
    }
  }

  return true;
}

export function checkWin(tiles: Tile[]): boolean {
  return tiles.some((t) => t.value === 2048 && !t.toDestroy);
}
