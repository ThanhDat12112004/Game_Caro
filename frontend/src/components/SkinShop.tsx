import React, { useEffect, useState } from 'react';
import { SkinService } from '../services/SkinService';
import { BoardSkin, PieceSkin, UserBalance, UserSkin } from '../types/Skin';
import './SkinShop.css';

interface SkinShopProps {
  userBalance: UserBalance | null;
  onBalanceUpdate: () => void;
  onClose: () => void;
}

const SkinShop: React.FC<SkinShopProps> = ({ userBalance, onBalanceUpdate, onClose }) => {
  const [activeTab, setActiveTab] = useState<'board' | 'piece'>('board');
  const [boardSkins, setBoardSkins] = useState<BoardSkin[]>([]);
  const [pieceSkins, setPieceSkins] = useState<PieceSkin[]>([]);
  const [userBoardSkins, setUserBoardSkins] = useState<UserSkin[]>([]);
  const [userPieceSkins, setUserPieceSkins] = useState<UserSkin[]>([]);
  const [loading, setLoading] = useState(true);
  const [purchasing, setPurchasing] = useState<string | null>(null);
  const [selecting, setSelecting] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    loadSkins();
  }, []);

  const loadSkins = async () => {
    try {
      setLoading(true);
      const [boardSkinsData, pieceSkinsData, userBoardSkinsData, userPieceSkinsData] =
        await Promise.all([
          SkinService.getAllBoardSkins(),
          SkinService.getAllPieceSkins(),
          SkinService.getUserBoardSkins(),
          SkinService.getUserPieceSkins(),
        ]);

      setBoardSkins(boardSkinsData);
      setPieceSkins(pieceSkinsData);
      setUserBoardSkins(userBoardSkinsData);
      setUserPieceSkins(userPieceSkinsData);
    } catch (error) {
      console.error('Error loading skins:', error);
      setMessage('Failed to load skins');
    } finally {
      setLoading(false);
    }
  };

  const handlePurchase = async (skinName: string, skinType: 'board' | 'piece') => {
    try {
      setPurchasing(skinName);
      setMessage(null);

      const response =
        skinType === 'board'
          ? await SkinService.purchaseBoardSkin(skinName)
          : await SkinService.purchasePieceSkin(skinName);

      if (response.success) {
        setMessage(`✅ ${response.message}`);
        await loadSkins(); // Reload to update owned skins
        onBalanceUpdate(); // Update balance in parent
      } else {
        setMessage(`❌ ${response.message}`);
      }
    } catch (error) {
      console.error('Error purchasing skin:', error);
      setMessage('❌ Failed to purchase skin');
    } finally {
      setPurchasing(null);
    }
  };

  const handleSelect = async (skinName: string, skinType: 'board' | 'piece') => {
    try {
      setSelecting(skinName);
      setMessage(null);

      const response =
        skinType === 'board'
          ? await SkinService.selectBoardSkin(skinName)
          : await SkinService.selectPieceSkin(skinName);

      if (response.success) {
        setMessage(`✅ ${response.message}`);
        onBalanceUpdate(); // Update selected skin info
      } else {
        setMessage(`❌ ${response.message}`);
      }
    } catch (error) {
      console.error('Error selecting skin:', error);
      setMessage('❌ Failed to select skin');
    } finally {
      setSelecting(null);
    }
  };

  const isOwned = (skinName: string, skinType: 'board' | 'piece') => {
    const ownedSkins = skinType === 'board' ? userBoardSkins : userPieceSkins;
    return ownedSkins.some((skin) => skin.skinName === skinName) || skinName === 'classic';
  };

  const isSelected = (skinName: string, skinType: 'board' | 'piece') => {
    if (!userBalance) return false;
    return skinType === 'board'
      ? userBalance.selectedBoardSkin === skinName
      : userBalance.selectedPieceSkin === skinName;
  };

  const canAfford = (price: number) => {
    return userBalance ? userBalance.balance >= price : false;
  };

  if (loading) {
    return (
      <div className="skin-shop-overlay">
        <div className="skin-shop">
          <div className="skin-shop-header">
            <h2>🛍️ Skin Shop</h2>
            <button className="close-button" onClick={onClose}>
              ✕
            </button>
          </div>
          <div className="loading-message">Loading skins...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="skin-shop-overlay">
      <div className="skin-shop">
        <div className="skin-shop-header">
          <h2>🛍️ Skin Shop</h2>
          <button className="close-button" onClick={onClose}>
            ✕
          </button>
        </div>

        {message && (
          <div className={`message ${message.includes('✅') ? 'success' : 'error'}`}>{message}</div>
        )}

        <div className="balance-display">
          <span className="balance-icon">💰</span>
          <span className="balance-amount">{userBalance?.balance || 0} Coins</span>
        </div>

        <div className="skin-tabs">
          <button
            className={`tab ${activeTab === 'board' ? 'active' : ''}`}
            onClick={() => setActiveTab('board')}
          >
            🎯 Board Skins
          </button>
          <button
            className={`tab ${activeTab === 'piece' ? 'active' : ''}`}
            onClick={() => setActiveTab('piece')}
          >
            ⚡ Piece Skins
          </button>
        </div>

        <div className="skins-grid">
          {activeTab === 'board' &&
            boardSkins.map((skin) => (
              <div
                key={skin.id}
                className={`skin-card ${isSelected(skin.name, 'board') ? 'selected' : ''}`}
              >
                <div
                  className="skin-preview board-preview"
                  style={{
                    backgroundColor: skin.backgroundColor || '#f9f9f9',
                    borderColor: skin.borderColor || '#333',
                  }}
                >
                  <div className="preview-grid">
                    {Array.from({ length: 9 }).map((_, i) => (
                      <div
                        key={i}
                        className="preview-cell"
                        style={{
                          backgroundColor: skin.cellColor || '#ffffff',
                          borderColor: skin.borderColor || '#ccc',
                        }}
                      >
                        {i === 4 ? 'X' : i === 0 ? 'O' : ''}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="skin-info">
                  <h3>{skin.displayName}</h3>
                  <p>{skin.description}</p>
                  <div className="skin-price">{skin.price === 0 ? 'Free' : `${skin.price} 💰`}</div>
                </div>

                <div className="skin-actions">
                  {isSelected(skin.name, 'board') ? (
                    <button className="selected-button" disabled>
                      ✅ Selected
                    </button>
                  ) : isOwned(skin.name, 'board') ? (
                    <button
                      className="select-button"
                      onClick={() => handleSelect(skin.name, 'board')}
                      disabled={selecting === skin.name}
                    >
                      {selecting === skin.name ? 'Selecting...' : 'Select'}
                    </button>
                  ) : (
                    <button
                      className={`purchase-button ${!canAfford(skin.price) ? 'disabled' : ''}`}
                      onClick={() => handlePurchase(skin.name, 'board')}
                      disabled={purchasing === skin.name || !canAfford(skin.price)}
                    >
                      {purchasing === skin.name ? 'Purchasing...' : 'Purchase'}
                    </button>
                  )}
                </div>
              </div>
            ))}

          {activeTab === 'piece' &&
            pieceSkins.map((skin) => (
              <div
                key={skin.id}
                className={`skin-card ${isSelected(skin.name, 'piece') ? 'selected' : ''}`}
              >
                <div className="skin-preview piece-preview">
                  <div className="piece-symbols">
                    <span
                      className="symbol-x emoji"
                      style={{
                        color: skin.xcolor || '#e74c3c',
                        backgroundColor: skin.xbackgroundColor || '#ffeaea',
                      }}
                    >
                      {skin.xsymbol}
                    </span>
                    <span
                      className="symbol-o emoji"
                      style={{
                        color: skin.ocolor || '#3498db',
                        backgroundColor: skin.obackgroundColor || '#eaf4fd',
                      }}
                    >
                      {skin.osymbol}
                    </span>
                  </div>
                </div>

                <div className="skin-info">
                  <h3>{skin.displayName}</h3>
                  <p>{skin.description}</p>
                  <div className="skin-price">{skin.price === 0 ? 'Free' : `${skin.price} 💰`}</div>
                </div>

                <div className="skin-actions">
                  {isSelected(skin.name, 'piece') ? (
                    <button className="selected-button" disabled>
                      ✅ Selected
                    </button>
                  ) : isOwned(skin.name, 'piece') ? (
                    <button
                      className="select-button"
                      onClick={() => handleSelect(skin.name, 'piece')}
                      disabled={selecting === skin.name}
                    >
                      {selecting === skin.name ? 'Selecting...' : 'Select'}
                    </button>
                  ) : (
                    <button
                      className={`purchase-button ${!canAfford(skin.price) ? 'disabled' : ''}`}
                      onClick={() => handlePurchase(skin.name, 'piece')}
                      disabled={purchasing === skin.name || !canAfford(skin.price)}
                    >
                      {purchasing === skin.name ? 'Purchasing...' : 'Purchase'}
                    </button>
                  )}
                </div>
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};

export default SkinShop;
