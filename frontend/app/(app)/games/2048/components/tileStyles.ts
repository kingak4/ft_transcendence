export type TileStyle = {
  bg: string;
  text: string;
  shadow?: string;
};

const BASE_TILE_STYLES: Record<number, TileStyle> = {
  2: {
    bg: 'bg-[#eee4da] text-[#776e65] dark:bg-[#313244] dark:text-[#cdd6f4] border border-black/5 dark:border-white/10',
    text: '',
    shadow: 'shadow-md',
  },
  4: {
    bg: 'bg-[#ede0c8] text-[#776e65] dark:bg-[#45475a] dark:text-[#cdd6f4] border border-black/5 dark:border-white/10',
    text: '',
    shadow: 'shadow-md',
  },
  8: {
    bg: 'bg-[#f2b179] dark:bg-amber-600',
    text: 'text-white',
    shadow: 'shadow-md',
  },
  16: {
    bg: 'bg-[#f59563] dark:bg-orange-600',
    text: 'text-white',
    shadow: 'shadow-md',
  },
  32: {
    bg: 'bg-[#f67c5f] dark:bg-red-500',
    text: 'text-white',
    shadow: 'shadow-md',
  },
  64: {
    bg: 'bg-[#e95938] dark:bg-rose-600',
    text: 'text-white',
    shadow: 'shadow-md',
  },
  128: {
    bg: 'bg-[#edcf72] dark:bg-yellow-500',
    text: 'text-white',
    shadow: 'shadow-[0_0_12px_rgba(237,207,114,0.6)]',
  },
  256: {
    bg: 'bg-[#edcc61] dark:bg-yellow-600',
    text: 'text-white',
    shadow: 'shadow-[0_0_16px_rgba(237,204,97,0.7)]',
  },
  512: {
    bg: 'bg-[#edc850] dark:bg-amber-500',
    text: 'text-white',
    shadow: 'shadow-[0_0_20px_rgba(237,200,80,0.8)]',
  },
  1024: {
    bg: 'bg-[#edc53f] dark:bg-amber-600',
    text: 'text-white',
    shadow: 'shadow-[0_0_24px_rgba(237,197,63,0.9)]',
  },
  2048: {
    bg: 'bg-hub-cta',
    text: 'text-hub-ink',
    shadow: 'shadow-[0_0_30px_rgba(74,201,160,0.9)] ring-2 ring-hub-lime/80',
  },
};

export function getTileStyle(value: number): TileStyle {
  if (BASE_TILE_STYLES[value]) {
    return BASE_TILE_STYLES[value];
  }

  return {
    bg: 'bg-gradient-to-br from-purple-700 to-indigo-900',
    text: 'text-amber-300',
    shadow: 'shadow-[0_0_30px_rgba(168,85,247,0.9)] ring-2 ring-amber-400/80',
  };
}
