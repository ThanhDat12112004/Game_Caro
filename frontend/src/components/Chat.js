import { Stomp } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import './Chat.css';

const Chat = ({ roomId, token }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [connected, setConnected] = useState(false);
  const [stompClient, setStompClient] = useState(null);
  const [connecting, setConnecting] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (token) {
      connectToChat();
    }

    return () => {
      if (stompClient) {
        console.log('Disconnecting STOMP client...');
        // Unsubscribe from chat subscription
        if (stompClient.chatSubscription) {
          stompClient.chatSubscription.unsubscribe();
        }
        stompClient.disconnect();
        setStompClient(null);
        setConnected(false);
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, roomId]);

  const connectToChat = () => {
    // Prevent multiple connection attempts
    if (connecting || connected) {
      console.log('Already connecting or connected, skipping...');
      return;
    }

    // Disconnect existing connection if any
    if (stompClient) {
      console.log('Disconnecting existing STOMP client...');
      stompClient.disconnect();
      setStompClient(null);
      setConnected(false);
    }

    setConnecting(true);
    const socket = new SockJS(`http://localhost:8080/ws?token=${token}`);
    const client = Stomp.over(socket);

    client.connect(
      { Authorization: `Bearer ${token}` },
      (frame) => {
        console.log('Connected to chat:', frame);
        setConnected(true);
        setConnecting(false);
        setStompClient(client);

        // Subscribe to chat messages
        const destination = roomId ? `/topic/chat/${roomId}` : '/topic/chat/global';
        const subscription = client.subscribe(destination, (message) => {
          const chatMessage = JSON.parse(message.body);
          console.log('Received chat message:', chatMessage);
          setMessages((prev) => {
            // Check if message already exists to prevent duplicates
            const exists = prev.some(
              (msg) =>
                msg.id === chatMessage.id ||
                (msg.content === chatMessage.content &&
                  msg.createdAt === chatMessage.createdAt &&
                  msg.sender?.username === chatMessage.sender?.username)
            );
            if (exists) {
              console.log('Duplicate message detected, skipping...');
              return prev;
            }
            return [...prev, chatMessage];
          });
        });

        // Store subscription for cleanup
        client.chatSubscription = subscription;

        // Send join message
        if (roomId) {
          client.send(`/app/chat/${roomId}/join`, {}, JSON.stringify({}));
        }
      },
      (error) => {
        console.error('Chat connection error:', error);
        setConnected(false);
        setConnecting(false);
      }
    );
  };

  const sendMessage = () => {
    if (newMessage.trim() && stompClient && connected) {
      const messagePayload = {
        content: newMessage.trim(),
      };

      const destination = roomId ? `/app/chat/${roomId}` : '/app/chat/global';
      stompClient.send(destination, {}, JSON.stringify(messagePayload));
      setNewMessage('');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString();
  };

  const getMessageTypeClass = (type) => {
    switch (type) {
      case 'JOIN':
        return 'system-message join';
      case 'LEAVE':
        return 'system-message leave';
      case 'SYSTEM':
        return 'system-message';
      default:
        return 'chat-message';
    }
  };

  return (
    <div className="chat-container">
      <div className="chat-header">
        <h3>{roomId ? `Room Chat: ${roomId}` : 'Global Chat'}</h3>
        <div className={`connection-status ${connected ? 'connected' : 'disconnected'}`}>
          {connecting ? '🟡 Connecting...' : connected ? '🟢 Connected' : '🔴 Disconnected'}
        </div>
      </div>

      <div className="chat-messages">
        {messages.map((message, index) => (
          <div key={index} className={`message ${getMessageTypeClass(message.type)}`}>
            {message.isSystemMessage ? (
              <div className="system-content">
                <span className="system-text">{message.content}</span>
                <span className="message-time">{formatTime(message.createdAt)}</span>
              </div>
            ) : (
              <div className="user-message">
                <div className="message-header">
                  <span className="sender-name">
                    {message.sender?.displayName || message.sender?.username || 'Unknown'}
                  </span>
                  <span className="message-time">{formatTime(message.createdAt)}</span>
                </div>
                <div className="message-content">{message.content}</div>
              </div>
            )}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input">
        <div className="input-group">
          <textarea
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={connected ? 'Type your message...' : 'Connecting...'}
            disabled={!connected}
            rows="2"
          />
          <button
            onClick={sendMessage}
            disabled={!connected || !newMessage.trim()}
            className="send-button"
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

export default Chat;
