import { useCallback, useEffect, useState } from 'react';
import './App.css';
import AdminPanel from './components/AdminPanel';
import BoardTypeSelector from './components/BoardTypeSelector';
import Chat from './components/Chat';
import GameBoard from './components/GameBoard';
import GameRoomList from './components/GameRoomList';
import Login from './components/Login';
import PlayerInfo from './components/PlayerInfo';
import Register from './components/Register';
import SkinShop from './components/SkinShop';
import UserBalance from './components/UserBalance';
import { AuthService } from './services/AuthService';
import { SkinService } from './services/SkinService';
import { WebSocketService } from './services/WebSocketService';
import { UserInfo } from './types/Auth';
import { BoardType } from './types/BoardType';
import { GameState, Move, Player } from './types/Game';
import { UserBalance as UserBalanceType } from './types/Skin';

type AuthMode = 'login' | 'register';
type AppMode = 'home' | 'board-selector' | 'game' | 'admin';

function App() {
  const [gameState, setGameState] = useState<GameState | null>(null);
  const [webSocketService, setWebSocketService] = useState<WebSocketService | null>(null);
  const [gameId, setGameId] = useState('');
  const [currentPlayerId, setCurrentPlayerId] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const [user, setUser] = useState<UserInfo | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authMode, setAuthMode] = useState<AuthMode>('login');
  const [loading, setLoading] = useState(true);
  const [selectedBoardType, setSelectedBoardType] = useState<BoardType>(BoardType.STANDARD);
  const [appMode, setAppMode] = useState<AppMode>('home');
  const [userBalance, setUserBalance] = useState<UserBalanceType | null>(null);
  const [showSkinShop, setShowSkinShop] = useState(false);

  const handleGameStateUpdate = useCallback((newGameState: GameState) => {
    setGameState(newGameState);
  }, []);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const userData = await AuthService.getCurrentUser();
        if (userData) {
          setUser(userData);
          setIsAuthenticated(true);
          // Load user balance after authentication
          loadUserBalance();
        }
      } catch (error) {
        console.error('Auth check error:', error);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  const handleLogin = () => {
    // Get user data from AuthService after successful login
    const getUserData = async () => {
      try {
        const userData = await AuthService.getCurrentUser();
        if (userData) {
          setUser(userData);
          setIsAuthenticated(true);
          // Load user balance after login
          loadUserBalance();
        }
      } catch (error) {
        console.error('Error getting current user after login:', error);
      }
    };
    getUserData();
  };

  const handleLogout = () => {
    AuthService.logout();
    setUser(null);
    setIsAuthenticated(false);
    setUserBalance(null);
    setShowSkinShop(false);
    if (webSocketService) {
      webSocketService.disconnect();
      setWebSocketService(null);
    }
    setIsConnected(false);
    setGameState(null);
    setCurrentPlayerId('');
    setAppMode('home');
  };

  const loadUserBalance = useCallback(async () => {
    try {
      const balance = await SkinService.getUserBalance();
      setUserBalance(balance);
    } catch (error) {
      console.error('Error loading user balance:', error);
    }
  }, []);

  const handleBalanceUpdate = useCallback(() => {
    loadUserBalance();
  }, [loadUserBalance]);

  const handleOpenSkinShop = () => {
    setShowSkinShop(true);
  };

  const handleCloseSkinShop = () => {
    setShowSkinShop(false);
  };

  const handleOpenAdminPanel = () => {
    setAppMode('admin');
  };

  const handleBackFromAdmin = () => {
    setAppMode('home');
  };

  const isUserAdmin = () => {
    return user?.role === 'ADMIN';
  };

  const connectToGame = (targetGameId?: string) => {
    const gameIdToUse = targetGameId || gameId;

    if (!gameIdToUse.trim() || !user) {
      alert('Vui lòng nhập mã phòng!');
      return;
    }

    const playerId = `player_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    setCurrentPlayerId(playerId);

    // Set gameId if it was provided
    if (targetGameId) {
      setGameId(targetGameId);
    }

    const handleRoomDeleted = () => {
      alert('Phòng đã bị xóa do không còn người chơi!');
      setAppMode('home');
      setWebSocketService(null);
      setGameState(null);
      setIsConnected(false);
    };

    const handlePlayerLeft = (leftPlayerId: string) => {
      console.log(`Player ${leftPlayerId} has left the game`);
      // You can add notification here if needed
    };

    const wsService = new WebSocketService(
      gameIdToUse,
      handleGameStateUpdate,
      handleRoomDeleted,
      handlePlayerLeft
    );
    setWebSocketService(wsService);

    const player: Player = {
      id: playerId,
      name: user.displayName,
      symbol: 'X',
      userId: user.username,
    };

    wsService.joinGame(player);
    setIsConnected(true);
    setAppMode('game');
  };

  const createNewGame = () => {
    setAppMode('board-selector');
    setGameId(`game_${Date.now()}`);
  };

  const handleBoardTypeSelected = (boardType: BoardType) => {
    setSelectedBoardType(boardType);
    connectToGame();
  };

  const handleJoinRoom = (roomId: string) => {
    connectToGame(roomId);
  };

  const handleBackToHome = () => {
    setAppMode('home');
    setGameId('');
  };

  const handleCellClick = (row: number, col: number) => {
    if (!webSocketService || !gameState || gameState.gameOver) return;

    const currentPlayer = gameState.players.find((p) => p.id === currentPlayerId);
    if (!currentPlayer || gameState.currentPlayer !== currentPlayerId) return;

    const move: Move = {
      row,
      col,
      playerId: currentPlayerId,
      symbol: currentPlayer.symbol,
    };

    webSocketService.makeMove(move);
  };

  const resetGame = () => {
    if (webSocketService) {
      webSocketService.resetGame();
    }
  };

  const handleConnectButtonClick = () => {
    connectToGame();
  };

  const leaveGame = () => {
    if (webSocketService && user && currentPlayerId) {
      const player: Player = {
        id: currentPlayerId,
        name: user.displayName,
        symbol: 'X', // Symbol doesn't matter for leaving
        userId: user.username,
      };

      webSocketService.leaveGame(player);
    }

    // Clean up local state
    setAppMode('home');
    setWebSocketService(null);
    setGameState(null);
    setIsConnected(false);
    setGameId('');
    setCurrentPlayerId('');
  };

  if (loading) {
    return (
      <div className="App">
        <div className="loading-container">
          <h2>Đang tải...</h2>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="App">
        {authMode === 'login' ? (
          <Login onLogin={handleLogin} onSwitchToRegister={() => setAuthMode('register')} />
        ) : (
          <Register onRegister={handleLogin} onSwitchToLogin={() => setAuthMode('login')} />
        )}
      </div>
    );
  }

  if (!isConnected) {
    if (appMode === 'admin') {
      return (
        <div className="App">
          <AdminPanel />
        </div>
      );
    }

    if (appMode === 'board-selector') {
      return (
        <div className="App">
          <div className="game-lobby">
            <h1>🎮 Game Caro Online</h1>
            <div className="user-info">
              <p>
                Xin chào, <strong>{user?.displayName}</strong>!
              </p>
              <button onClick={handleLogout} className="logout-button">
                Đăng xuất
              </button>
            </div>

            <BoardTypeSelector
              selectedBoardType={selectedBoardType}
              onBoardTypeChange={handleBoardTypeSelected}
            />
            <div className="lobby-form">
              <p>
                <strong>Mã phòng:</strong> {gameId}
              </p>
              <button onClick={handleBackToHome} className="back-button">
                ← Quay lại trang chủ
              </button>
            </div>
          </div>
        </div>
      );
    }

    return (
      <div className="App">
        <div className="game-lobby">
          <h1>🎮 Game Caro Online</h1>
          <div className="user-info">
            <p>
              Xin chào, <strong>{user?.displayName}</strong>!
            </p>
            <div className="user-actions">
              <button onClick={handleOpenSkinShop} className="skin-shop-button">
                🛍️ Skin Shop
              </button>
              {isUserAdmin() && (
                <button onClick={handleOpenAdminPanel} className="admin-button">
                  ⚙️ Admin Panel
                </button>
              )}
              <button onClick={handleLogout} className="logout-button">
                Đăng xuất
              </button>
            </div>
          </div>

          <UserBalance onBalanceUpdate={handleBalanceUpdate} />

          <GameRoomList onJoinRoom={handleJoinRoom} onCreateRoom={createNewGame} />

          <div className="manual-join">
            <div className="divider">hoặc tham gia trực tiếp</div>
            <div className="lobby-form">
              <input
                type="text"
                placeholder="Nhập mã phòng để tham gia trực tiếp"
                value={gameId}
                onChange={(e) => setGameId(e.target.value)}
                className="input-field"
              />
              <button onClick={handleConnectButtonClick} className="connect-button">
                Tham gia phòng
              </button>
            </div>
          </div>
        </div>

        {showSkinShop && (
          <SkinShop
            userBalance={userBalance}
            onBalanceUpdate={handleBalanceUpdate}
            onClose={handleCloseSkinShop}
          />
        )}
      </div>
    );
  }

  return (
    <div className="App">
      <div className="game-container">
        <h1>🎮 Game Caro Online</h1>
        <div className="game-header">
          <div className="game-info">
            <p>
              <strong>Phòng:</strong> {gameId}
            </p>
            <p>
              <strong>Người chơi:</strong> {user?.displayName}
            </p>
          </div>
          <div className="game-controls">
            <button onClick={resetGame} className="reset-button">
              🔄 Chơi lại
            </button>
            <button onClick={leaveGame} className="disconnect-button">
              🚪 Thoát phòng
            </button>
            <button onClick={handleLogout} className="logout-button">
              Đăng xuất
            </button>
          </div>
        </div>

        {gameState && (
          <>
            <PlayerInfo
              players={gameState.players}
              currentPlayer={gameState.currentPlayer}
              gameOver={gameState.gameOver}
              winner={gameState.winner}
            />

            <div className="game-content">
              <div className="game-board-container">
                <GameBoard
                  board={gameState.board}
                  onCellClick={handleCellClick}
                  disabled={gameState.gameOver || gameState.currentPlayer !== currentPlayerId}
                  boardSize={gameState.board.length}
                  boardSkin={userBalance?.selectedBoardSkin || 'classic'}
                  pieceSkin={userBalance?.selectedPieceSkin || 'classic'}
                />
              </div>

              <div className="chat-container">
                <Chat roomId={gameId} token={localStorage.getItem('token')} />
              </div>
            </div>
          </>
        )}

        {showSkinShop && (
          <SkinShop
            userBalance={userBalance}
            onBalanceUpdate={handleBalanceUpdate}
            onClose={handleCloseSkinShop}
          />
        )}
      </div>
    </div>
  );
}

export default App;
