export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  displayName: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  displayName: string;
  email: string;
}

export interface UserInfo {
  username: string;
  displayName: string;
  email: string;
  role?: string;
}
