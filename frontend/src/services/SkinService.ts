import { BoardSkin, PieceSkin, PurchaseResponse, UserBalance, UserSkin } from '../types/Skin';
import { AuthService } from './AuthService';

const API_BASE_URL = 'http://localhost:8080/api/skins';

export class SkinService {
  private static getAuthHeaders() {
    const token = AuthService.getToken();
    return {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    };
  }

  // Board Skins
  static async getAllBoardSkins(): Promise<BoardSkin[]> {
    const response = await fetch(`${API_BASE_URL}/board`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch board skins');
    }

    return await response.json();
  }

  static async getUserBoardSkins(): Promise<UserSkin[]> {
    const response = await fetch(`${API_BASE_URL}/user/board`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch user board skins');
    }

    return await response.json();
  }

  static async purchaseBoardSkin(skinName: string): Promise<PurchaseResponse> {
    const response = await fetch(`${API_BASE_URL}/purchase/board/${skinName}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to purchase board skin');
    }

    return await response.json();
  }

  static async selectBoardSkin(skinName: string): Promise<PurchaseResponse> {
    const response = await fetch(`${API_BASE_URL}/select/board/${skinName}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to select board skin');
    }

    return await response.json();
  }

  // Piece Skins
  static async getAllPieceSkins(): Promise<PieceSkin[]> {
    const response = await fetch(`${API_BASE_URL}/piece`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch piece skins');
    }

    return await response.json();
  }

  static async getUserPieceSkins(): Promise<UserSkin[]> {
    const response = await fetch(`${API_BASE_URL}/user/piece`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch user piece skins');
    }

    return await response.json();
  }

  static async purchasePieceSkin(skinName: string): Promise<PurchaseResponse> {
    const response = await fetch(`${API_BASE_URL}/purchase/piece/${skinName}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to purchase piece skin');
    }

    return await response.json();
  }

  static async getPieceSkinByName(skinName: string): Promise<PieceSkin | null> {
    try {
      const allPieceSkins = await this.getAllPieceSkins();
      return allPieceSkins.find((skin) => skin.name === skinName) || null;
    } catch (error) {
      console.error('Failed to get piece skin by name:', error);
      return null;
    }
  }

  static async selectPieceSkin(skinName: string): Promise<PurchaseResponse> {
    const response = await fetch(`${API_BASE_URL}/select/piece/${skinName}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to select piece skin');
    }

    return await response.json();
  }

  // User Balance and Stats
  static async getUserBalance(): Promise<UserBalance> {
    const response = await fetch(`${API_BASE_URL}/user/balance`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch user balance');
    }

    return await response.json();
  }
}
