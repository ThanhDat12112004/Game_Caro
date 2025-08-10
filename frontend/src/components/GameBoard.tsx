import React, { useEffect, useState } from 'react';
import { SkinService } from '../services/SkinService';
import { PieceSkin } from '../types/Skin';
import './BoardSkins.css';
import './GameBoard.css';

interface GameBoardProps {
  board: (string | null)[][];
  onCellClick: (row: number, col: number) => void;
  disabled?: boolean;
  boardSize?: number;
  boardSkin?: string;
  pieceSkin?: string;
}

const GameBoard: React.FC<GameBoardProps> = ({
  board,
  onCellClick,
  disabled = false,
  boardSize = 15,
  boardSkin = 'classic',
  pieceSkin = 'classic',
}) => {
  const [pieceSkinData, setPieceSkinData] = useState<PieceSkin | null>(null);

  useEffect(() => {
    const loadPieceSkin = async () => {
      try {
        const skinData = await SkinService.getPieceSkinByName(pieceSkin);
        setPieceSkinData(skinData);
      } catch (error) {
        console.error('Error loading piece skin:', error);
      }
    };

    loadPieceSkin();
  }, [pieceSkin]);

  // Calculate cell size based on board size
  const getCellSize = () => {
    if (boardSize <= 10) return '40px';
    if (boardSize <= 15) return '30px';
    if (boardSize <= 20) return '25px';
    if (boardSize <= 25) return '20px';
    if (boardSize <= 30) return '18px';
    return '15px';
  };

  const cellSize = getCellSize();

  // Get piece symbols based on piece skin
  const getPieceSymbol = (symbol: string) => {
    if (!pieceSkinData) {
      // Fallback to default symbols if skin data not loaded
      return symbol;
    }

    if (symbol === 'X') {
      return pieceSkinData.xsymbol || 'X';
    } else if (symbol === 'O') {
      return pieceSkinData.osymbol || 'O';
    }

    return symbol;
  };

  // Get piece colors based on piece skin
  const getPieceColor = (symbol: string) => {
    if (!pieceSkinData) {
      return symbol === 'X' ? '#dc3545' : '#007bff';
    }

    if (symbol === 'X') {
      return pieceSkinData.xcolor || '#dc3545';
    } else if (symbol === 'O') {
      return pieceSkinData.ocolor || '#007bff';
    }

    return '#333';
  };

  // Get piece background colors based on piece skin
  const getPieceBackgroundColor = (symbol: string) => {
    if (!pieceSkinData) {
      return 'transparent';
    }

    if (symbol === 'X') {
      return pieceSkinData.xbackgroundColor || 'transparent';
    } else if (symbol === 'O') {
      return pieceSkinData.obackgroundColor || 'transparent';
    }

    return 'transparent';
  };

  return (
    <div className={`board-skin-${boardSkin}`}>
      <div
        className="game-board"
        style={{
          gridTemplateColumns: `repeat(${boardSize}, ${cellSize})`,
          gridTemplateRows: `repeat(${boardSize}, ${cellSize})`,
        }}
      >
        {board.map((row, rowIndex) =>
          row.map((cell, colIndex) => (
            <button
              key={`${rowIndex}-${colIndex}`}
              className={`board-cell ${cell ? 'filled' : ''} ${
                cell === 'X' ? 'x' : cell === 'O' ? 'o' : ''
              } piece-skin-${pieceSkin} emoji`}
              onClick={() => !disabled && !cell && onCellClick(rowIndex, colIndex)}
              disabled={disabled || !!cell}
              style={{
                width: cellSize,
                height: cellSize,
                fontSize: boardSize > 20 ? '10px' : '14px',
                color: cell ? getPieceColor(cell) : 'inherit',
                backgroundColor: cell ? getPieceBackgroundColor(cell) : 'transparent',
              }}
            >
              <span className="piece-symbol">{cell ? getPieceSymbol(cell) : ''}</span>
            </button>
          ))
        )}
      </div>
    </div>
  );
};

export default GameBoard;
