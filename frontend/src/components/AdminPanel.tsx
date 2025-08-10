import React, { useEffect, useState } from 'react';
import {
  AdminBoardSkin,
  AdminBoardType,
  AdminPieceSkin,
  AdminStats,
  AdminUser,
} from '../types/Admin';
import './AdminPanel.css';
import Modal from './Modal';

const AdminPanel = () => {
  const [currentTab, setCurrentTab] = useState('users');
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [boardTypes, setBoardTypes] = useState<AdminBoardType[]>([]);
  const [boardSkins, setBoardSkins] = useState<AdminBoardSkin[]>([]);
  const [pieceSkins, setPieceSkins] = useState<AdminPieceSkin[]>([]);
  const [stats, setStats] = useState<AdminStats>({
    totalUsers: 0,
    totalActiveUsers: 0,
    totalBoardTypes: 0,
    totalBoardSkins: 0,
    totalPieceSkins: 0,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Modal states
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState<'add' | 'edit'>('add');
  const [editingItem, setEditingItem] = useState<any>(null);
  const [currentForm, setCurrentForm] = useState<'user' | 'boardType' | 'boardSkin' | 'pieceSkin'>(
    'user'
  );

  // Form states
  const [formData, setFormData] = useState<any>({});

  const token = localStorage.getItem('token');

  const apiCall = async (url: string, method: string = 'GET', body: any = null): Promise<any> => {
    try {
      const response = await fetch(`http://localhost:8080${url}`, {
        method,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: body ? JSON.stringify(body) : null,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return method === 'DELETE' ? null : await response.json();
    } catch (error: any) {
      console.error('API call failed:', error);
      throw error;
    }
  };

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      switch (currentTab) {
        case 'users':
          const usersData = await apiCall('/api/admin/users');
          setUsers(usersData);
          break;
        case 'board-types':
          const boardTypesData = await apiCall('/api/admin/board-types');
          setBoardTypes(boardTypesData);
          break;
        case 'board-skins':
          const boardSkinsData = await apiCall('/api/admin/board-skins');
          setBoardSkins(boardSkinsData);
          break;
        case 'piece-skins':
          const pieceSkinsData = await apiCall('/api/admin/piece-skins');
          setPieceSkins(pieceSkinsData);
          break;
        case 'stats':
          const statsData = await apiCall('/api/admin/stats');
          setStats(statsData);
          break;
      }
    } catch (error: any) {
      setError('Failed to load data: ' + (error?.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  const deleteUser = async (userId: number) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await apiCall(`/api/admin/users/${userId}`, 'DELETE');
        await loadData();
      } catch (error: any) {
        setError('Failed to delete user: ' + (error?.message || 'Unknown error'));
      }
    }
  };

  const toggleUserStatus = async (userId: number) => {
    try {
      await apiCall(`/api/admin/users/${userId}/toggle-status`, 'PUT');
      await loadData();
    } catch (error: any) {
      setError('Failed to toggle user status: ' + (error?.message || 'Unknown error'));
    }
  };

  const deleteItem = async (type: string, id: number) => {
    if (window.confirm(`Are you sure you want to delete this ${type}?`)) {
      try {
        await apiCall(`/api/admin/${type}/${id}`, 'DELETE');
        await loadData();
      } catch (error: any) {
        setError(`Failed to delete ${type}: ` + (error?.message || 'Unknown error'));
      }
    }
  };

  useEffect(() => {
    loadData();
  }, [currentTab]);

  // Modal functions
  const openAddModal = (formType: 'user' | 'boardType' | 'boardSkin' | 'pieceSkin') => {
    setCurrentForm(formType);
    setModalType('add');
    setEditingItem(null);
    setFormData(getDefaultFormData(formType));
    setIsModalOpen(true);
  };

  const openEditModal = (formType: 'user' | 'boardType' | 'boardSkin' | 'pieceSkin', item: any) => {
    setCurrentForm(formType);
    setModalType('edit');
    setEditingItem(item);

    // Merge item with default data to ensure all fields are defined
    const defaultData = getDefaultFormData(formType);
    const mergedData = { ...defaultData, ...item };
    setFormData(mergedData);

    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingItem(null);
    setFormData({});
  };

  const getDefaultFormData = (formType: string) => {
    switch (formType) {
      case 'user':
        return {
          username: '',
          email: '',
          displayName: '',
          role: 'USER',
          enabled: true,
          balance: 0,
          password: '',
        };
      case 'boardType':
        return {
          name: '',
          displayName: '',
          boardSize: 15,
          winCondition: 5,
          isDefault: false,
          isActive: true,
          description: '',
        };
      case 'boardSkin':
        return {
          name: '',
          displayName: '',
          description: '',
          price: 0,
          cssClass: '',
          backgroundColor: '#ffffff',
          borderColor: '#000000',
          cellColor: '#f0f0f0',
          hoverColor: '#e0e0e0',
          isPremium: false,
          isActive: true,
        };
      case 'pieceSkin':
        return {
          name: '',
          displayName: '',
          description: '',
          price: 0,
          xsymbol: 'X',
          osymbol: 'O',
          xcolor: '#ff0000',
          ocolor: '#0000ff',
          xbackgroundColor: '#ffffff',
          obackgroundColor: '#ffffff',
          cssClass: '',
          animationClass: '',
          isPremium: false,
          isActive: true,
        };
      default:
        return {};
    }
  };

  const handleFormSubmit = async (submitData: any) => {
    try {
      console.log('Form submit data:', submitData);
      console.log('Current form:', currentForm);
      console.log('Modal type:', modalType);

      const baseUrl = `/api/admin/${
        currentForm === 'user'
          ? 'users'
          : currentForm === 'boardType'
          ? 'board-types'
          : currentForm === 'boardSkin'
          ? 'board-skins'
          : 'piece-skins'
      }`;

      console.log('API URL:', baseUrl);

      if (modalType === 'add') {
        const result = await apiCall(baseUrl, 'POST', submitData);
        console.log('Add result:', result);
      } else {
        const result = await apiCall(`${baseUrl}/${editingItem.id}`, 'PUT', submitData);
        console.log('Update result:', result);
      }

      closeModal();
      await loadData();
    } catch (error: any) {
      console.error('Form submit error:', error);
      setError(`Failed to ${modalType} ${currentForm}: ` + (error?.message || 'Unknown error'));
    }
  };

  const renderUsers = () => (
    <div className="admin-table-container">
      <div className="table-header">
        <h3>User Management</h3>
        <button className="btn btn-primary" onClick={() => openAddModal('user')}>
          Add New User
        </button>
      </div>
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Display Name</th>
            <th>Balance</th>
            <th>Role</th>
            <th>Status</th>
            <th>Total Games</th>
            <th>Total Wins</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.displayName || user.username}</td>
              <td>{user.balance || 0}</td>
              <td>{user.role}</td>
              <td>
                <span className={`status ${user.enabled ? 'active' : 'disabled'}`}>
                  {user.enabled ? 'Active' : 'Disabled'}
                </span>
              </td>
              <td>{user.totalGames || 0}</td>
              <td>{user.totalWins || 0}</td>
              <td>
                <button
                  onClick={() => openEditModal('user', user)}
                  className="btn btn-secondary"
                  style={{ marginRight: '8px' }}
                >
                  Edit
                </button>
                <button
                  onClick={() => toggleUserStatus(user.id)}
                  className={`btn ${user.enabled ? 'btn-warning' : 'btn-success'}`}
                  style={{ marginRight: '8px' }}
                >
                  {user.enabled ? 'Disable' : 'Enable'}
                </button>
                <button onClick={() => deleteUser(user.id)} className="btn btn-danger">
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  const renderBoardTypes = () => (
    <div className="admin-table-container">
      <div className="table-header">
        <h3>Board Types Management</h3>
        <button className="btn btn-primary" onClick={() => openAddModal('boardType')}>
          Add New Board Type
        </button>
      </div>
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Display Name</th>
            <th>Board Size</th>
            <th>Win Condition</th>
            <th>Status</th>
            <th>Default</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {boardTypes.map((boardType) => (
            <tr key={boardType.id}>
              <td>{boardType.id}</td>
              <td>{boardType.name}</td>
              <td>{boardType.displayName}</td>
              <td>
                {boardType.boardSize}x{boardType.boardSize}
              </td>
              <td>{boardType.winCondition || 5}</td>
              <td>
                <span className={`status ${boardType.isActive ? 'active' : 'disabled'}`}>
                  {boardType.isActive ? 'Active' : 'Disabled'}
                </span>
              </td>
              <td>{boardType.isDefault ? 'Yes' : 'No'}</td>
              <td>
                <button
                  onClick={() => openEditModal('boardType', boardType)}
                  className="btn btn-secondary"
                  style={{ marginRight: '8px' }}
                >
                  Edit
                </button>
                <button
                  onClick={() => deleteItem('board-types', boardType.id)}
                  className="btn btn-danger"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  const renderBoardSkins = () => (
    <div className="admin-table-container">
      <div className="table-header">
        <h3>Board Skins Management</h3>
        <button className="btn btn-primary" onClick={() => openAddModal('boardSkin')}>
          Add New Board Skin
        </button>
      </div>
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Display Name</th>
            <th>Price</th>
            <th>Premium</th>
            <th>Status</th>
            <th>Background</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {boardSkins.map((skin) => (
            <tr key={skin.id}>
              <td>{skin.id}</td>
              <td>{skin.name}</td>
              <td>{skin.displayName}</td>
              <td>{skin.price}</td>
              <td>{skin.isPremium ? 'Yes' : 'No'}</td>
              <td>
                <span className={`status ${skin.isActive ? 'active' : 'disabled'}`}>
                  {skin.isActive ? 'Active' : 'Disabled'}
                </span>
              </td>
              <td>
                <div
                  className="color-preview"
                  style={{ backgroundColor: skin.backgroundColor }}
                ></div>
              </td>
              <td>
                <button
                  onClick={() => openEditModal('boardSkin', skin)}
                  className="btn btn-secondary"
                  style={{ marginRight: '8px' }}
                >
                  Edit
                </button>
                <button
                  onClick={() => deleteItem('board-skins', skin.id)}
                  className="btn btn-danger"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  const renderPieceSkins = () => (
    <div className="admin-table-container">
      <div className="table-header">
        <h3>Piece Skins Management</h3>
        <button className="btn btn-primary" onClick={() => openAddModal('pieceSkin')}>
          Add New Piece Skin
        </button>
      </div>
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Display Name</th>
            <th>Price</th>
            <th>X Symbol</th>
            <th>O Symbol</th>
            <th>Premium</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {pieceSkins.map((skin) => (
            <tr key={skin.id}>
              <td>{skin.id}</td>
              <td>{skin.name}</td>
              <td>{skin.displayName}</td>
              <td>{skin.price}</td>
              <td className="symbol-preview" style={{ color: skin.xcolor }}>
                <span className="emoji" title={`X Symbol: ${skin.xsymbol}`}>
                  {skin.xsymbol}
                </span>
              </td>
              <td className="symbol-preview" style={{ color: skin.ocolor }}>
                <span className="emoji" title={`O Symbol: ${skin.osymbol}`}>
                  {skin.osymbol}
                </span>
              </td>
              <td>{skin.isPremium ? 'Yes' : 'No'}</td>
              <td>
                <span className={`status ${skin.isActive ? 'active' : 'disabled'}`}>
                  {skin.isActive ? 'Active' : 'Disabled'}
                </span>
              </td>
              <td>
                <button
                  onClick={() => openEditModal('pieceSkin', skin)}
                  className="btn btn-secondary"
                  style={{ marginRight: '8px' }}
                >
                  Edit
                </button>
                <button
                  onClick={() => deleteItem('piece-skins', skin.id)}
                  className="btn btn-danger"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  const renderStats = () => (
    <div className="admin-stats-container">
      <h3>Admin Statistics</h3>
      <div className="stats-grid">
        <div className="stat-card">
          <h4>Total Users</h4>
          <p className="stat-number">{stats.totalUsers}</p>
        </div>
        <div className="stat-card">
          <h4>Active Users</h4>
          <p className="stat-number">{stats.totalActiveUsers}</p>
        </div>
        <div className="stat-card">
          <h4>Board Types</h4>
          <p className="stat-number">{stats.totalBoardTypes}</p>
        </div>
        <div className="stat-card">
          <h4>Board Skins</h4>
          <p className="stat-number">{stats.totalBoardSkins}</p>
        </div>
        <div className="stat-card">
          <h4>Piece Skins</h4>
          <p className="stat-number">{stats.totalPieceSkins}</p>
        </div>
      </div>
    </div>
  );

  const renderContent = () => {
    if (loading) return <div className="loading">Loading...</div>;
    if (error) return <div className="error">{error}</div>;

    switch (currentTab) {
      case 'users':
        return renderUsers();
      case 'board-types':
        return renderBoardTypes();
      case 'board-skins':
        return renderBoardSkins();
      case 'piece-skins':
        return renderPieceSkins();
      case 'stats':
        return renderStats();
      default:
        return <div>Select a tab</div>;
    }
  };

  // Form Components
  const renderUserForm = () => {
    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      // Remove password if editing and password is empty
      const submitData: any = { ...formData };
      if (modalType === 'edit' && !submitData.password) {
        delete submitData.password;
      }
      handleFormSubmit(submitData);
    };

    return (
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username:</label>
          <input
            type="text"
            value={formData.username}
            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
            required
            disabled={modalType === 'edit'}
          />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            required
          />
        </div>
        <div className="form-group">
          <label>Display Name:</label>
          <input
            type="text"
            value={formData.displayName}
            onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Role:</label>
            <select
              value={formData.role}
              onChange={(e) => setFormData({ ...formData, role: e.target.value })}
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <div className="form-group">
            <label>Balance:</label>
            <input
              type="number"
              value={formData.balance}
              onChange={(e) => setFormData({ ...formData, balance: parseInt(e.target.value) })}
              min="0"
            />
          </div>
        </div>
        <div className="form-group">
          <label>Password {modalType === 'edit' ? '(leave empty to keep current)' : ''}:</label>
          <input
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            required={modalType === 'add'}
          />
        </div>
        <div className="form-group">
          <div className="checkbox-group">
            <input
              type="checkbox"
              checked={formData.enabled}
              onChange={(e) => setFormData({ ...formData, enabled: e.target.checked })}
            />
            <label>Enabled</label>
          </div>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={closeModal}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            {modalType === 'add' ? 'Create' : 'Update'}
          </button>
        </div>
      </form>
    );
  };

  const renderBoardTypeForm = () => {
    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      handleFormSubmit(formData);
    };

    return (
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Name:</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Display Name:</label>
            <input
              type="text"
              value={formData.displayName}
              onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Board Size:</label>
            <input
              type="number"
              value={formData.boardSize}
              onChange={(e) => setFormData({ ...formData, boardSize: parseInt(e.target.value) })}
              min="10"
              max="30"
              required
            />
          </div>
          <div className="form-group">
            <label>Win Condition:</label>
            <input
              type="number"
              value={formData.winCondition}
              onChange={(e) => setFormData({ ...formData, winCondition: parseInt(e.target.value) })}
              min="3"
              max="10"
              required
            />
          </div>
        </div>
        <div className="form-group">
          <label>Description:</label>
          <textarea
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isDefault}
                onChange={(e) => setFormData({ ...formData, isDefault: e.target.checked })}
              />
              <label>Default Board Type</label>
            </div>
          </div>
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isActive}
                onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
              />
              <label>Active</label>
            </div>
          </div>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={closeModal}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            {modalType === 'add' ? 'Create' : 'Update'}
          </button>
        </div>
      </form>
    );
  };

  const renderBoardSkinForm = () => {
    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      handleFormSubmit(formData);
    };

    return (
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Name:</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Display Name:</label>
            <input
              type="text"
              value={formData.displayName}
              onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="form-group">
          <label>Description:</label>
          <textarea
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Price:</label>
            <input
              type="number"
              value={formData.price}
              onChange={(e) => setFormData({ ...formData, price: parseInt(e.target.value) || 0 })}
              min="0"
              required
            />
          </div>
          <div className="form-group">
            <label>CSS Class:</label>
            <input
              type="text"
              value={formData.cssClass}
              onChange={(e) => setFormData({ ...formData, cssClass: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Background Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.backgroundColor}
                onChange={(e) => setFormData({ ...formData, backgroundColor: e.target.value })}
              />
              <div
                className="color-preview"
                style={{ backgroundColor: formData.backgroundColor }}
              ></div>
            </div>
          </div>
          <div className="form-group">
            <label>Border Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.borderColor}
                onChange={(e) => setFormData({ ...formData, borderColor: e.target.value })}
              />
              <div
                className="color-preview"
                style={{ backgroundColor: formData.borderColor }}
              ></div>
            </div>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Cell Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.cellColor}
                onChange={(e) => setFormData({ ...formData, cellColor: e.target.value })}
              />
              <div className="color-preview" style={{ backgroundColor: formData.cellColor }}></div>
            </div>
          </div>
          <div className="form-group">
            <label>Hover Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.hoverColor}
                onChange={(e) => setFormData({ ...formData, hoverColor: e.target.value })}
              />
              <div className="color-preview" style={{ backgroundColor: formData.hoverColor }}></div>
            </div>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isPremium}
                onChange={(e) => setFormData({ ...formData, isPremium: e.target.checked })}
              />
              <label>Premium Skin</label>
            </div>
          </div>
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isActive}
                onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
              />
              <label>Active</label>
            </div>
          </div>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={closeModal}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            {modalType === 'add' ? 'Create' : 'Update'}
          </button>
        </div>
      </form>
    );
  };

  const renderPieceSkinForm = () => {
    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      handleFormSubmit(formData);
    };

    return (
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Name:</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Display Name:</label>
            <input
              type="text"
              value={formData.displayName}
              onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="form-group">
          <label>Description:</label>
          <textarea
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Price:</label>
            <input
              type="number"
              value={formData.price}
              onChange={(e) => setFormData({ ...formData, price: parseInt(e.target.value) || 0 })}
              min="0"
              required
            />
          </div>
          <div className="form-group">
            <label>CSS Class:</label>
            <input
              type="text"
              value={formData.cssClass}
              onChange={(e) => setFormData({ ...formData, cssClass: e.target.value })}
            />
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>X Symbol:</label>
            <div className="symbol-input-group">
              <input
                type="text"
                value={formData.xsymbol || 'X'}
                onChange={(e) => setFormData({ ...formData, xsymbol: e.target.value })}
                maxLength={3}
                required
              />
              <div
                className="symbol-preview"
                style={{
                  color: formData.xcolor || '#ff0000',
                  backgroundColor: formData.xbackgroundColor || '#ffffff',
                }}
              >
                {formData.xsymbol || 'X'}
              </div>
            </div>
          </div>
          <div className="form-group">
            <label>O Symbol:</label>
            <div className="symbol-input-group">
              <input
                type="text"
                value={formData.osymbol || 'O'}
                onChange={(e) => setFormData({ ...formData, osymbol: e.target.value })}
                maxLength={3}
                required
              />
              <div
                className="symbol-preview"
                style={{
                  color: formData.ocolor || '#0000ff',
                  backgroundColor: formData.obackgroundColor || '#ffffff',
                }}
              >
                {formData.osymbol || 'O'}
              </div>
            </div>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>X Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.xcolor}
                onChange={(e) => setFormData({ ...formData, xcolor: e.target.value })}
              />
              <div className="color-preview" style={{ backgroundColor: formData.xcolor }}></div>
            </div>
          </div>
          <div className="form-group">
            <label>O Color:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.ocolor}
                onChange={(e) => setFormData({ ...formData, ocolor: e.target.value })}
              />
              <div className="color-preview" style={{ backgroundColor: formData.ocolor }}></div>
            </div>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>X Background:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.xbackgroundColor}
                onChange={(e) => setFormData({ ...formData, xbackgroundColor: e.target.value })}
              />
              <div
                className="color-preview"
                style={{ backgroundColor: formData.xbackgroundColor }}
              ></div>
            </div>
          </div>
          <div className="form-group">
            <label>O Background:</label>
            <div className="color-input-group">
              <input
                type="color"
                value={formData.obackgroundColor}
                onChange={(e) => setFormData({ ...formData, obackgroundColor: e.target.value })}
              />
              <div
                className="color-preview"
                style={{ backgroundColor: formData.obackgroundColor }}
              ></div>
            </div>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isPremium}
                onChange={(e) => setFormData({ ...formData, isPremium: e.target.checked })}
              />
              <label>Premium Skin</label>
            </div>
          </div>
          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                checked={formData.isActive}
                onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
              />
              <label>Active</label>
            </div>
          </div>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={closeModal}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            {modalType === 'add' ? 'Create' : 'Update'}
          </button>
        </div>
      </form>
    );
  };

  const renderModalForm = () => {
    switch (currentForm) {
      case 'user':
        return renderUserForm();
      case 'boardType':
        return renderBoardTypeForm();
      case 'boardSkin':
        return renderBoardSkinForm();
      case 'pieceSkin':
        return renderPieceSkinForm();
      default:
        return <div>Unknown form type</div>;
    }
  };

  return (
    <div className="admin-panel">
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <button onClick={() => (window.location.href = '/')} className="btn btn-secondary">
          Back to Game
        </button>
      </div>

      <div className="admin-tabs">
        <button
          className={`tab ${currentTab === 'stats' ? 'active' : ''}`}
          onClick={() => setCurrentTab('stats')}
        >
          Statistics
        </button>
        <button
          className={`tab ${currentTab === 'users' ? 'active' : ''}`}
          onClick={() => setCurrentTab('users')}
        >
          Users
        </button>
        <button
          className={`tab ${currentTab === 'board-types' ? 'active' : ''}`}
          onClick={() => setCurrentTab('board-types')}
        >
          Board Types
        </button>
        <button
          className={`tab ${currentTab === 'board-skins' ? 'active' : ''}`}
          onClick={() => setCurrentTab('board-skins')}
        >
          Board Skins
        </button>
        <button
          className={`tab ${currentTab === 'piece-skins' ? 'active' : ''}`}
          onClick={() => setCurrentTab('piece-skins')}
        >
          Piece Skins
        </button>
      </div>

      <div className="admin-content">{renderContent()}</div>

      {/* Modal for Add/Edit Forms */}
      <Modal
        isOpen={isModalOpen}
        onClose={closeModal}
        title={`${modalType === 'add' ? 'Add New' : 'Edit'} ${
          currentForm === 'user'
            ? 'User'
            : currentForm === 'boardType'
            ? 'Board Type'
            : currentForm === 'boardSkin'
            ? 'Board Skin'
            : 'Piece Skin'
        }`}
      >
        {renderModalForm()}
      </Modal>
    </div>
  );
};

export default AdminPanel;
