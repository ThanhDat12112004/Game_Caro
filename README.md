# 🎮 Game Caro Online - Đồ Án J2EE

## 📖 Giới Thiệu

Game Caro Online là một ứng dụng web hiện đại cho phép người chơi tham gia trò chơi cờ caro trực tuyến với bạn bè. Dự án được xây dựng với kiến trúc Client-Server sử dụng các công nghệ J2EE tiên tiến.

### ✨ Tính Năng Chính

- 🎯 **Game Caro Real-time**: Chơi cờ caro trực tuyến với đồng bộ tức thời
- 👥 **Đa người chơi**: Hỗ trợ nhiều phòng game đồng thời
- 🔐 **Hệ thống xác thực**: Đăng ký, đăng nhập an toàn với JWT
- 🎨 **Cửa hàng skin**: Mua sắm giao diện game với tiền ảo
- 💰 **Hệ thống coin**: Tích lũy và sử dụng tiền ảo
- 🛡️ **Quản trị hệ thống**: Panel admin quản lý người dùng
- 📱 **Responsive Design**: Giao diện thích ứng mọi thiết bị

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   React + TS    │◄──►│  Spring Boot    │◄──►│  PostgreSQL     │
│   WebSocket     │    │   WebSocket     │    │   JPA/Hibernate │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Spring Boot 3.2.0** - Framework chính
- **Spring Security** - Bảo mật và xác thực
- **Spring WebSocket** - Giao tiếp real-time
- **Spring Data JPA** - Quản lý cơ sở dữ liệu
- **PostgreSQL** - Cơ sở dữ liệu chính
- **JWT** - Token xác thực
- **Maven** - Quản lý dependencies

### Frontend
- **React 19.1.0** - Framework UI
- **TypeScript 4.9.5** - Ngôn ngữ lập trình
- **STOMP.js** - WebSocket client
- **SockJS** - WebSocket fallback
- **CSS3** - Styling và animation

## 🚀 Cài Đặt và Chạy Dự Án

### Yêu Cầu Hệ Thống
- **Java 17+**
- **Node.js 16+**
- **PostgreSQL 13+**
- **Maven 3.6+**

### 1. Thiết Lập Database

```sql
-- Tạo database
CREATE DATABASE caro_game;
```

### 2. Cấu Hình Backend

```bash
# Di chuyển vào thư mục backend
cd backend

# Cập nhật file application.properties nếu cần
# spring.datasource.url=
# spring.datasource.username=
# spring.datasource.password=

# Cài đặt dependencies và chạy
./mvnw clean install
./mvnw spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### 3. Cấu Hình Frontend

```bash
# Di chuyển vào thư mục frontend
cd frontend

# Cài đặt dependencies
npm install

# Chạy development server
npm start
```

Frontend sẽ chạy tại: `http://localhost:3000`

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/logout` - Đăng xuất

### Game Management
- `GET /api/rooms` - Lấy danh sách phòng
- `POST /api/rooms` - Tạo phòng mới
- `POST /api/rooms/{id}/join` - Tham gia phòng

### User Management
- `GET /api/users/profile` - Thông tin người dùng
- `PUT /api/users/profile` - Cập nhật profile
- `GET /api/users/balance` - Số dư coin

### Shop System
- `GET /api/skins` - Danh sách skin
- `POST /api/skins/{id}/buy` - Mua skin

## 🎯 Use Cases Chính

### 1. Người Chơi (Player)
- Đăng ký/Đăng nhập tài khoản
- Tìm kiếm và tham gia phòng game
- Chơi game caro với người khác
- Mua sắm skin trong cửa hàng
- Xem lịch sử trận đấu

### 2. Quản Trị Viên (Admin)
- Quản lý danh sách người dùng
- Theo dõi hoạt động hệ thống
- Quản lý cửa hàng và skin
- Xử lý báo cáo và khiếu nại

## 🔗 WebSocket Events

### Game Events
```javascript
// Tham gia phòng
stompClient.send("/app/join-room", {}, JSON.stringify({roomId: 1}));

// Thực hiện nước đi
stompClient.send("/app/make-move", {}, JSON.stringify({
    roomId: 1, 
    row: 5, 
    col: 5
}));

// Lắng nghe cập nhật game
stompClient.subscribe("/topic/room/1", function(message) {
    const gameUpdate = JSON.parse(message.body);
    // Xử lý cập nhật game
});
```

## 📊 Database Schema

### Các Bảng Chính
- **users** - Thông tin người dùng
- **game_rooms** - Phòng game
- **game_moves** - Lịch sử nước đi
- **skins** - Giao diện game
- **user_skins** - Skin của người dùng
- **user_stats** - Thống kê người chơi

## 🧪 Testing

### Backend Testing
```bash
cd backend
./mvnw test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## 🔧 Development

### Code Structure
```
caro_game/
├── backend/
│   ├── src/main/java/com/example/carogame/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access
│   │   ├── entity/         # JPA Entities
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── config/         # Configurations
│   │   └── security/       # Security Config
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/   # Flyway Scripts
└── frontend/
    ├── src/
    │   ├── components/     # React Components
    │   ├── services/       # API Services
    │   └── types/          # TypeScript Types
    └── public/
```

## 🚀 Deployment

### Production Build
```bash
# Build backend
cd backend
./mvnw clean package

# Build frontend
cd frontend
npm run build
```

## 📝 License

Dự án này được phát triển cho mục đích học tập - Đồ án J2EE.

## 👥 Author

- **Developer**: Dương Thành Đạt
- **Course**: Lập trình Java Enterprise (J2EE)
- **Year**: 2025

## 📞 Liên Hệ

---

⭐ **Nếu project hữu ích, hãy cho một star!** ⭐
