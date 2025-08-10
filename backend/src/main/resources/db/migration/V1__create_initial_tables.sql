-- Create initial database schema
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    balance INTEGER DEFAULT 1000,
    total_games INTEGER DEFAULT 0,
    total_wins INTEGER DEFAULT 0,
    selected_board_skin VARCHAR(50) DEFAULT 'classic',
    selected_piece_skin VARCHAR(50) DEFAULT 'classic',
    enabled BOOLEAN DEFAULT true,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create board types table
CREATE TABLE board_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    board_size INTEGER NOT NULL,
    win_condition INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_default BOOLEAN DEFAULT false
);

-- Create board skins table
CREATE TABLE board_skins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    background_color VARCHAR(7) DEFAULT '#f5f5dc',
    border_color VARCHAR(7) DEFAULT '#8b4513',
    cell_color VARCHAR(7) DEFAULT '#ffffff',
    hover_color VARCHAR(7) DEFAULT '#e0e0e0',
    css_class VARCHAR(50),
    price INTEGER DEFAULT 0,
    is_premium BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true
);

-- Create piece skins table
CREATE TABLE piece_skins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    x_symbol VARCHAR(10) DEFAULT 'X',
    o_symbol VARCHAR(10) DEFAULT 'O',
    x_color VARCHAR(7) DEFAULT '#ff0000',
    o_color VARCHAR(7) DEFAULT '#0000ff',
    x_background_color VARCHAR(7),
    o_background_color VARCHAR(7),
    css_class VARCHAR(50),
    animation_class VARCHAR(50),
    price INTEGER DEFAULT 0,
    is_premium BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true
);

-- Create user skins table
CREATE TABLE user_skins (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    skin_name VARCHAR(50) NOT NULL,
    skin_type VARCHAR(20) NOT NULL,
    purchase_price INTEGER NOT NULL,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create chat messages table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    room_id VARCHAR(255),
    type VARCHAR(20) NOT NULL DEFAULT 'CHAT',
    is_system_message BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_skins_user_id ON user_skins(user_id);
CREATE INDEX idx_user_skins_skin_type ON user_skins(skin_type);
CREATE INDEX idx_chat_messages_room_id ON chat_messages(room_id);
CREATE INDEX idx_chat_messages_created_at ON chat_messages(created_at);
CREATE INDEX idx_chat_messages_sender_id ON chat_messages(sender_id);
