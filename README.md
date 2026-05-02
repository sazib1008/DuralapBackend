# Duralap Backend - Messaging & Video Calling

A production-ready WhatsApp/Telegram-like messaging and video calling backend built with Spring Boot Kotlin.

## Features

- ✅ **User Authentication** - JWT-based auth with OAuth2 support
- ✅ **Conversation Requests** - WhatsApp-style accept/reject flow
- ✅ **Real-time Messaging** - Kafka-powered message delivery
- ✅ **Video/Audio Calling** - WebRTC signaling via Redis Pub/Sub
- ✅ **User Presence** - Redis-based online/offline status
- ✅ **Rate Limiting** - Redis-based spam protection
- ✅ **Caching** - Redis caching for optimal performance
- ✅ **WebSocket** - Real-time communication

## Tech Stack

- **Backend**: Spring Boot 4.0.3, Kotlin 2.2.21
- **Database**: MongoDB (primary storage)
- **Cache**: Redis (caching, rate limiting, presence)
- **Messaging**: Kafka (event streaming)
- **Security**: JWT, Spring Security, OAuth2
- **Real-time**: WebSocket, WebRTC

## Quick Start

### Prerequisites
- Java 21+
- MongoDB
- Redis
- Kafka (optional, for messaging)

### Run Locally

```bash
# Clone repository
git clone <repository-url>
cd DuralapBackend-master

# Run with default settings (uses embedded defaults)
./mvnw spring-boot:run

# Or build and run
./mvnw clean package -DskipTests
java -jar target/duralap-0.0.1-SNAPSHOT.jar
```

### Production Deployment

```bash
# Set environment variables
export MONGODB_URI="your-mongodb-connection-string"
export REDIS_HOST="your-redis-host"
export REDIS_PASSWORD="your-redis-password"
export JWT_SECRET="your-secure-jwt-secret-min-64-chars"
export SERVER_PORT="8080"
export KAFKA_BOOTSTRAP_SERVERS="your-kafka-brokers"

# Run with environment variables
java -jar target/duralap-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

```bash
# Using docker-compose
docker-compose up -d

# Or build custom image
docker build -t duralap-backend .
docker run -p 8080:8080 \
  -e MONGODB_URI="..." \
  -e REDIS_HOST="..." \
  duralap-backend
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login with credentials
- `POST /api/auth/refresh` - Refresh JWT token

### Users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/search?query=...` - Search users
- `PUT /api/users/{id}` - Update user profile

### Conversations
- `POST /api/conversations/start-with` - Start conversation (creates request)
- `GET /api/conversations/my` - Get my conversations
- `GET /api/conversations/{id}` - Get conversation details
- `DELETE /api/conversations/{id}` - Delete conversation

### Conversation Requests
- `GET /api/conversation-requests/pending` - Get pending requests
- `POST /api/conversation-requests/accept` - Accept request
- `POST /api/conversation-requests/reject` - Reject request
- `POST /api/conversation-requests/cancel` - Cancel sent request

### Messages
- `POST /api/messages` - Send message
- `GET /api/messages/{conversationId}` - Get messages (paginated)
- `PUT /api/messages/{id}/read` - Mark as read

### Calls
- `POST /api/cals/initiate` - Initiate call
- `POST /api/calls/{id}/accept` - Accept call
- `POST /api/calls/{id}/reject` - Reject call
- `POST /api/calls/{id}/end` - End call

## Configuration

### application.properties

Key configuration options:

```properties
# Server
server.port=8080

# MongoDB
spring.data.mongodb.uri=${MONGODB_URI}

# Redis
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.password=${REDIS_PASSWORD}

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-in-ms=86400000

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS}
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `MONGODB_URI` | MongoDB connection string | Yes |
| `REDIS_HOST` | Redis server host | Yes |
| `REDIS_PORT` | Redis server port | No (default: 6379) |
| `REDIS_PASSWORD` | Redis password | Yes |
| `JWT_SECRET` | JWT signing secret (min 64 chars) | Yes |
| `SERVER_PORT` | Server port | No (default: 8080) |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka brokers | No |

## Performance

### Optimizations Applied
- **Database Indexes**: 9+ compound indexes for fast queries
- **Redis Caching**: User profile caching (30min TTL)
- **Connection Pooling**: MongoDB (100), Redis (20), Tomcat (200 threads)
- **Rate Limiting**: 60 msgs/min, 10 requests/hour
- **Compression**: GZIP enabled (60-70% smaller responses)
- **Kafka**: Batch processing, Snappy compression

### Benchmarks
- User lookup: 1-5ms (cached)
- Message send: 20-40ms
- Concurrent users: ~5,000
- Response compression: 60-70%

## Architecture

```
┌─────────────┐
│   Client    │ (Android/Web)
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│  Spring Boot    │
│  REST API + WS  │
└──────┬──────────┘
       │
       ├──────────────┐
       ▼              ▼
┌─────────────┐ ┌──────────┐
│  MongoDB    │ │  Redis   │
│  (Storage)  │ │ (Cache)  │
└─────────────┘ └──────────┘
       │
       ▼
┌─────────────┐
│   Kafka     │
│  (Events)   │
└─────────────┘
```

## Security

- JWT-based authentication
- Password encryption (BCrypt)
- Rate limiting (Redis)
- Input validation
- CORS configuration
- OAuth2 support (Google, Facebook, GitHub)

## Monitoring

### Health Checks
- `GET /actuator/health` - Application health
- `GET /actuator/metrics` - Performance metrics
- `GET /actuator/prometheus` - Prometheus metrics

### Logs
- Application logs: `logs/duralap.log`
- Access logs: Configurable
- Log level: INFO (production)

## Development

### Build
```bash
./mvnw clean compile
```

### Test
```bash
./mvnw test
```

### Package
```bash
./mvnw clean package -DskipTests
```

## Project Structure

```
src/main/kotlin/com/example/duralap/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── database/         # Models, DTOs, repositories
├── consumers/        # Kafka consumers
├── events/           # Event classes
├── exception/        # Error handling
├── security/         # Security configuration
├── service/          # Business logic
│   ├── cache/        # Caching services
│   └── signaling/    # WebRTC signaling
└── DuralapApplication.kt
```

## License

MIT License

## Support

For issues and questions, please create an issue in the repository.
