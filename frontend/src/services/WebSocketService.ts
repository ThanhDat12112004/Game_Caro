import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { GameState, Move, Player } from '../types/Game';

export class WebSocketService {
  private client: Client;
  private gameId: string;
  private onGameStateUpdate: (gameState: GameState) => void;
  private onRoomDeleted?: () => void;
  private onPlayerLeft?: (playerId: string) => void;
  private isConnected: boolean = false;
  private pendingActions: (() => void)[] = [];

  constructor(
    gameId: string,
    onGameStateUpdate: (gameState: GameState) => void,
    onRoomDeleted?: () => void,
    onPlayerLeft?: (playerId: string) => void
  ) {
    this.gameId = gameId;
    this.onGameStateUpdate = onGameStateUpdate;
    this.onRoomDeleted = onRoomDeleted;
    this.onPlayerLeft = onPlayerLeft;

    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        console.log('Connected to WebSocket');
        this.isConnected = true;
        this.subscribeToGame();
        // Execute any pending actions
        this.pendingActions.forEach((action) => action());
        this.pendingActions = [];
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        this.isConnected = false;
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        this.isConnected = false;
      },
    });

    // Auto connect when service is created
    this.connect();
  }

  connect(): void {
    if (!this.isConnected) {
      this.client.activate();
    }
  }

  disconnect(): void {
    this.isConnected = false;
    this.client.deactivate();
  }

  private executeWhenConnected(action: () => void): void {
    if (this.isConnected) {
      action();
    } else {
      this.pendingActions.push(action);
    }
  }

  private subscribeToGame(): void {
    // Subscribe to game state updates
    this.client.subscribe(`/topic/game/${this.gameId}`, (message) => {
      const gameState: GameState = JSON.parse(message.body);
      this.onGameStateUpdate(gameState);
    });

    // Subscribe to room deletion events
    this.client.subscribe(`/topic/game/${this.gameId}/deleted`, (message) => {
      console.log('Room deleted:', message.body);
      if (this.onRoomDeleted) {
        this.onRoomDeleted();
      }
    });

    // Subscribe to player left events
    this.client.subscribe(`/topic/game/${this.gameId}/player-left`, (message) => {
      const data = JSON.parse(message.body);
      console.log('Player left:', data);
      if (this.onPlayerLeft) {
        this.onPlayerLeft(data.playerId);
      }
    });
  }

  joinGame(player: Player): void {
    this.executeWhenConnected(() => {
      this.client.publish({
        destination: `/app/game/${this.gameId}/join`,
        body: JSON.stringify(player),
      });
    });
  }

  makeMove(move: Move): void {
    this.executeWhenConnected(() => {
      this.client.publish({
        destination: `/app/game/${this.gameId}/move`,
        body: JSON.stringify(move),
      });
    });
  }

  resetGame(): void {
    this.executeWhenConnected(() => {
      this.client.publish({
        destination: `/app/game/${this.gameId}/reset`,
        body: JSON.stringify({}),
      });
    });
  }

  leaveGame(player: Player): void {
    this.executeWhenConnected(() => {
      this.client.publish({
        destination: `/app/game/${this.gameId}/leave`,
        body: JSON.stringify(player),
      });
    });
  }
}
