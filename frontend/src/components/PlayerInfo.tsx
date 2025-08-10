import React from 'react';
import { Player } from '../types/Game';
import './PlayerInfo.css';

interface PlayerInfoProps {
  players: Player[];
  currentPlayer: string;
  gameOver: boolean;
  winner: string | null;
}

const PlayerInfo: React.FC<PlayerInfoProps> = ({ players, currentPlayer, gameOver, winner }) => {
  const getPlayerBySymbol = (symbol: 'X' | 'O') => {
    return players.find((player) => player.symbol === symbol);
  };

  const playerX = getPlayerBySymbol('X');
  const playerO = getPlayerBySymbol('O');

  return (
    <div className="player-info">
      <h3>Thông tin người chơi</h3>

      <div className="players-container">
        {playerX && (
          <div className={`player ${currentPlayer === playerX.id ? 'current-player' : ''}`}>
            <span className="player-symbol x">X</span>
            <span className="player-name">{playerX.name}</span>
            {currentPlayer === playerX.id && !gameOver && (
              <span className="turn-indicator">← Lượt của bạn</span>
            )}
          </div>
        )}

        {playerO && (
          <div className={`player ${currentPlayer === playerO.id ? 'current-player' : ''}`}>
            <span className="player-symbol o">O</span>
            <span className="player-name">{playerO.name}</span>
            {currentPlayer === playerO.id && !gameOver && (
              <span className="turn-indicator">← Lượt của bạn</span>
            )}
          </div>
        )}
      </div>

      {gameOver && winner && (
        <div className="game-result">
          <h4>🎉 Game Over!</h4>
          <p>Người thắng: {players.find((p) => p.id === winner)?.name}</p>
        </div>
      )}

      {players.length < 2 && (
        <div className="waiting-message">
          <p>Đang đợi người chơi thứ 2...</p>
        </div>
      )}
    </div>
  );
};

export default PlayerInfo;
