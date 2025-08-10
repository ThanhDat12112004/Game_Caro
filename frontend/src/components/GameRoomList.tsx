import React, { useState, useEffect } from 'react';
import { GameRoomInfo, GameRoomsResponse } from '../types/GameRoom';
import './GameRoomList.css';

interface GameRoomListProps {
  onJoinRoom: (gameId: string) => void;
  onCreateRoom: () => void;
}

const GameRoomList: React.FC<GameRoomListProps> = ({ onJoinRoom, onCreateRoom }) => {
  const [rooms, setRooms] = useState<GameRoomInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/games');
      if (!response.ok) {
        throw new Error('Không thể tải danh sách phòng');
      }
      const data: GameRoomsResponse = await response.json();
      setRooms(data.games);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi không xác định');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
    // Auto refresh every 5 seconds
    const interval = setInterval(fetchRooms, 5000);
    return () => clearInterval(interval);
  }, []);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PLAYING': return '🎮';
      case 'FINISHED': return '🏁';
      default: return '⏳';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'PLAYING': return 'Đang chơi';
      case 'FINISHED': return 'Đã kết thúc';
      default: return 'Chờ người chơi';
    }
  };

  if (loading) {
    return (
      <div className="room-list-container">
        <div className="loading">🔄 Đang tải danh sách phòng...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="room-list-container">
        <div className="error">❌ {error}</div>
        <button onClick={fetchRooms} className="retry-button">
          🔄 Thử lại
        </button>
      </div>
    );
  }

  return (
    <div className="room-list-container">
      <div className="room-list-header">
        <h2>🏠 Danh sách phòng chơi</h2>
        <div className="room-list-actions">
          <button onClick={fetchRooms} className="refresh-button">
            🔄 Làm mới
          </button>
          <button onClick={onCreateRoom} className="create-room-button">
            ➕ Tạo phòng mới
          </button>
        </div>
      </div>

      {rooms.length === 0 ? (
        <div className="no-rooms">
          <div className="no-rooms-content">
            <h3>🎮 Chưa có phòng nào</h3>
            <p>Hãy tạo phòng đầu tiên để bắt đầu chơi!</p>
            <button onClick={onCreateRoom} className="create-first-room-button">
              🚀 Tạo phòng đầu tiên
            </button>
          </div>
        </div>
      ) : (
        <div className="rooms-grid">
          {rooms.map((room) => (
            <div key={room.gameId} className={`room-card ${!room.canJoin ? 'room-full' : ''}`}>
              <div className="room-header">
                <div className="room-id">#{room.gameId.substring(5, 13)}</div>
                <div className={`room-status ${room.gameStatus.toLowerCase()}`}>
                  {getStatusIcon(room.gameStatus)} {getStatusText(room.gameStatus)}
                </div>
              </div>
              
              <div className="room-info">
                <div className="board-info">
                  <span className="board-type">{room.boardDescription}</span>
                  <span className="board-size">{room.boardSize}x{room.boardSize}</span>
                </div>
                
                <div className="player-info">
                  <span className={`player-count ${room.playerCount === room.maxPlayers ? 'full' : ''}`}>
                    👥 {room.playerCount}/{room.maxPlayers}
                  </span>
                </div>
              </div>

              <div className="room-actions">
                {room.canJoin ? (
                  <button 
                    onClick={() => onJoinRoom(room.gameId)}
                    className="join-button"
                  >
                    🚪 Vào phòng
                  </button>
                ) : (
                  <button 
                    onClick={() => onJoinRoom(room.gameId)}
                    className="spectate-button"
                  >
                    👁️ Xem trận đấu
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GameRoomList;
