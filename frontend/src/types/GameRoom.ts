export interface GameRoomInfo {
  gameId: string;
  boardType: string;
  boardSize: number;
  boardDescription: string;
  playerCount: number;
  maxPlayers: number;
  gameStatus: 'PLAYING' | 'FINISHED' | 'WAITING';
  canJoin: boolean;
}

export interface GameRoomsResponse {
  games: GameRoomInfo[];
  totalGames: number;
}
