export type Tile = {
  id: string;
  value: number;
  r: number;
  c: number;
  isNew?: boolean;
  isMerged?: boolean;
  toDestroy?: boolean;
};

let tileIdCounter = 0;
function getNextId() {
  return `tile-${tileIdCounter++}`;
}

export function initializeBoard(): Tile[] {
  tileIdCounter = 0;
  const tiles: Tile[] = [];
  addRandomTile(tiles);
  addRandomTile(tiles);
  return tiles;
}

export function addRandomTile(tiles: Tile[]): boolean {
  const emptyCells = [];
  for (let r = 0; r < 4; r++) {
    for (let c = 0; c < 4; c++) {
      if (!tiles.find((t) => t.r === r && t.c === c && !t.toDestroy)) {
        emptyCells.push({ r, c });
      }
    }
  }
  if (emptyCells.length === 0) return false;
  const { r, c } = emptyCells[Math.floor(Math.random() * emptyCells.length)];
  tiles.push({
    id: getNextId(),
    value: Math.random() < 0.9 ? 2 : 4,
    r,
    c,
    isNew: true,
  });
  return true;
}

export function moveTiles(
  tiles: Tile[],
  direction: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT',
): { newTiles: Tile[]; changed: boolean; score: number } {
  const newTiles = tiles
    .filter((t) => !t.toDestroy)
    .map((t) => ({ ...t, isNew: false, isMerged: false }));

  let changed = false;
  let score = 0;

  const grid: (Tile | null)[][] = Array(4)
    .fill(null)
    .map(() => Array(4).fill(null));

  newTiles.forEach((t) => {
    grid[t.r][t.c] = t;
  });

  const moveVector = {
    UP: { dr: -1, dc: 0 },
    DOWN: { dr: 1, dc: 0 },
    LEFT: { dr: 0, dc: -1 },
    RIGHT: { dr: 0, dc: 1 },
  }[direction];

  const rTraverse = direction === 'DOWN' ? [3, 2, 1, 0] : [0, 1, 2, 3];
  const cTraverse = direction === 'RIGHT' ? [3, 2, 1, 0] : [0, 1, 2, 3];

  for (const r of rTraverse) {
    for (const c of cTraverse) {
      const tile = grid[r][c];
      if (!tile) continue;

      let currR = r;
      let currC = c;
      let nextR = currR + moveVector.dr;
      let nextC = currC + moveVector.dc;

      while (nextR >= 0 && nextR < 4 && nextC >= 0 && nextC < 4) {
        const nextTile = grid[nextR][nextC];
        if (nextTile === null) {
          currR = nextR;
          currC = nextC;
          nextR += moveVector.dr;
          nextC += moveVector.dc;
        } else if (
          nextTile.value === tile.value &&
          !nextTile.isMerged &&
          !nextTile.toDestroy
        ) {
          currR = nextR;
          currC = nextC;
          break;
        } else {
          break;
        }
      }

      if (currR !== r || currC !== c) {
        changed = true;
        const targetTile = grid[currR][currC];
        if (
          targetTile &&
          targetTile.value === tile.value &&
          !targetTile.isMerged &&
          !targetTile.toDestroy
        ) {
          targetTile.value *= 2;
          targetTile.isMerged = true;
          score += targetTile.value;

          tile.r = currR;
          tile.c = currC;
          tile.toDestroy = true;

          grid[r][c] = null;
        } else {
          grid[r][c] = null;
          grid[currR][currC] = tile;
          tile.r = currR;
          tile.c = currC;
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
    grid[t.r][t.c] = t;
  });

  for (let r = 0; r < 4; r++) {
    for (let c = 0; c < 4; c++) {
      const val = grid[r][c]?.value;
      if (!val) return false;
      if (c < 3 && grid[r][c + 1]?.value === val) return false;
      if (r < 3 && grid[r + 1][c]?.value === val) return false;
    }
  }
  return true;
}

export function checkWin(tiles: Tile[]): boolean {
  return tiles.some((t) => t.value === 2048 && !t.toDestroy);
}
