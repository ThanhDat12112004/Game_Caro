import React from 'react';
import { BoardType, BOARD_TYPES } from '../types/BoardType';
import './BoardTypeSelector.css';

interface BoardTypeSelectorProps {
  selectedBoardType: BoardType;
  onBoardTypeChange: (boardType: BoardType) => void;
}

const BoardTypeSelector: React.FC<BoardTypeSelectorProps> = ({
  selectedBoardType,
  onBoardTypeChange
}) => {
  return (
    <div className="board-type-selector">
      <h3>Chọn loại bàn cờ:</h3>
      <div className="board-type-grid">
        {BOARD_TYPES.map((boardInfo) => (
          <div
            key={boardInfo.type}
            className={`board-type-card ${
              selectedBoardType === boardInfo.type ? 'selected' : ''
            }`}
            onClick={() => onBoardTypeChange(boardInfo.type)}
          >
            <div className="board-preview">
              <div 
                className="mini-board"
                style={{
                  display: 'grid',
                  gridTemplateColumns: `repeat(${Math.min(boardInfo.size, 8)}, 1fr)`,
                  gridTemplateRows: `repeat(${Math.min(boardInfo.size, 8)}, 1fr)`,
                }}
              >
                {Array.from({ length: Math.min(boardInfo.size * boardInfo.size, 64) }).map((_, index) => (
                  <div key={index} className="mini-cell"></div>
                ))}
              </div>
            </div>
            <div className="board-info">
              <h4>{boardInfo.description}</h4>
              <p>{boardInfo.size}x{boardInfo.size} ô</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BoardTypeSelector;
