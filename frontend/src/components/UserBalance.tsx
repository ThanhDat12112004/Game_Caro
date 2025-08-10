import React, { useEffect, useState, useCallback } from 'react';
import { SkinService } from '../services/SkinService';
import { UserBalance as UserBalanceType } from '../types/Skin';
import './UserBalance.css';

interface UserBalanceProps {
  onBalanceUpdate?: (balance: number) => void;
}

const UserBalance: React.FC<UserBalanceProps> = ({ onBalanceUpdate }) => {
  const [userBalance, setUserBalance] = useState<UserBalanceType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchUserBalance = useCallback(async () => {
    try {
      setLoading(true);
      const balance = await SkinService.getUserBalance();
      setUserBalance(balance);
      setError(null);
    } catch (err) {
      setError('Failed to load user balance');
      console.error('Error fetching user balance:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  // Call onBalanceUpdate when userBalance changes
  useEffect(() => {
    if (userBalance && onBalanceUpdate) {
      onBalanceUpdate(userBalance.balance);
    }
  }, [userBalance, onBalanceUpdate]);

  useEffect(() => {
    fetchUserBalance();
  }, [fetchUserBalance]);

  if (loading) {
    return (
      <div className="user-balance loading">
        <div className="balance-item">
          <span className="balance-label">Loading...</span>
        </div>
      </div>
    );
  }

  if (error || !userBalance) {
    return (
      <div className="user-balance error">
        <div className="balance-item">
          <span className="balance-label">Error loading balance</span>
        </div>
      </div>
    );
  }

  return (
    <div className="user-balance">
      <div className="balance-item coins">
        <span className="balance-icon">💰</span>
        <span className="balance-value">{userBalance.balance}</span>
        <span className="balance-label">Coins</span>
      </div>

      <div className="balance-item wins">
        <span className="balance-icon">🏆</span>
        <span className="balance-value">{userBalance.totalWins}</span>
        <span className="balance-label">Wins</span>
      </div>

      <div className="balance-item games">
        <span className="balance-icon">🎮</span>
        <span className="balance-value">{userBalance.totalGames}</span>
        <span className="balance-label">Games</span>
      </div>

      <div className="balance-item winrate">
        <span className="balance-icon">📊</span>
        <span className="balance-value">{userBalance.winRate.toFixed(1)}%</span>
        <span className="balance-label">Win Rate</span>
      </div>
    </div>
  );
};

export default UserBalance;
