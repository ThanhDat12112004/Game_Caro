import { AuthResponse, LoginRequest, RegisterRequest, UserInfo } from '../types/Auth';

const API_BASE_URL = 'http://localhost:8080/api/auth';

export class AuthService {
  static async login(request: LoginRequest): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Login failed');
    }

    const authResponse = await response.json();
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem(
      'user',
      JSON.stringify({
        username: authResponse.username,
        displayName: authResponse.displayName,
        email: authResponse.email,
      })
    );

    return authResponse;
  }

  static async register(request: RegisterRequest): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Registration failed');
    }

    const authResponse = await response.json();
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem(
      'user',
      JSON.stringify({
        username: authResponse.username,
        displayName: authResponse.displayName,
        email: authResponse.email,
      })
    );

    return authResponse;
  }

  static async getCurrentUser(): Promise<UserInfo | null> {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const response = await fetch(`${API_BASE_URL}/me`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        this.logout();
        return null;
      }

      return await response.json();
    } catch (error) {
      this.logout();
      return null;
    }
  }

  static logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  static getToken(): string | null {
    return localStorage.getItem('token');
  }

  static getStoredUser(): UserInfo | null {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;

    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  static isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
