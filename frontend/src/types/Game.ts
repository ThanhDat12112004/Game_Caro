export interface Player {
  id: string;
  name: string;
  symbol: 'X' | 'O';
  userId?: string;
}

export interface Move {
  row: number;
  col: number;
  playerId: string;
  symbol: 'X' | 'O';
}

export interface GameState {
  board: (string | null)[][];
  currentPlayer: string;
  gameOver: boolean;
  winner: string | null;
  players: Player[];
  gameId: string;
}
