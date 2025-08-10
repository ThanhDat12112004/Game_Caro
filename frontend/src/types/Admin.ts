export interface AdminUser {
  id: number;
  username: string;
  email: string;
  displayName?: string;
  role: string;
  enabled: boolean;
  balance?: number;
  totalGames?: number;
  totalWins?: number;
  createdAt: string;
  lastLogin?: string;
}

export interface AdminBoardType {
  id: number;
  name: string;
  displayName: string;
  boardSize: number;
  winCondition: number;
  isDefault: boolean;
  isActive: boolean;
  description?: string;
}

export interface AdminBoardSkin {
  id: number;
  name: string;
  displayName: string;
  description: string;
  price: number;
  cssClass: string;
  backgroundColor?: string;
  borderColor?: string;
  cellColor?: string;
  hoverColor?: string;
  isPremium: boolean;
  isActive: boolean;
}

export interface AdminPieceSkin {
  id: number;
  name: string;
  displayName: string;
  description: string;
  price: number;
  xsymbol: string;
  osymbol: string;
  xcolor?: string;
  ocolor?: string;
  xbackgroundColor?: string;
  obackgroundColor?: string;
  cssClass?: string;
  isPremium: boolean;
  isActive: boolean;
  animationClass?: string;
}

export interface AdminStats {
  totalUsers: number;
  totalActiveUsers: number;
  totalBoardTypes: number;
  totalBoardSkins: number;
  totalPieceSkins: number;
}
