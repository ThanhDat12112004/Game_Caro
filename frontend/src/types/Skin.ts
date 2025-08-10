export interface BoardSkin {
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

export interface PieceSkin {
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

export interface UserSkin {
  id: number;
  skinType: 'BOARD' | 'PIECE';
  skinName: string;
  purchasedAt: string;
  purchasePrice: number;
}

export interface UserBalance {
  balance: number;
  totalWins: number;
  totalGames: number;
  winRate: number;
  selectedBoardSkin: string;
  selectedPieceSkin: string;
}

export interface PurchaseResponse {
  success: boolean;
  message: string;
  newBalance?: number;
}
