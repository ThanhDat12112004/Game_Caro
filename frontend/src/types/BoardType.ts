export enum BoardType {
  SMALL = 'SMALL',
  STANDARD = 'STANDARD', 
  LARGE = 'LARGE',
  EXTRA_LARGE = 'EXTRA_LARGE',
  HUGE = 'HUGE',
  MASSIVE = 'MASSIVE'
}

export interface BoardTypeInfo {
  type: BoardType;
  size: number;
  description: string;
}

export const BOARD_TYPES: BoardTypeInfo[] = [
  { type: BoardType.SMALL, size: 10, description: '10x10 - Nhỏ' },
  { type: BoardType.STANDARD, size: 15, description: '15x15 - Tiêu chuẩn' },
  { type: BoardType.LARGE, size: 20, description: '20x20 - Lớn' },
  { type: BoardType.EXTRA_LARGE, size: 25, description: '25x25 - Rất lớn' },
  { type: BoardType.HUGE, size: 30, description: '30x30 - Khổng lồ' },
  { type: BoardType.MASSIVE, size: 40, description: '40x40 - Siêu lớn' }
];
