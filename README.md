# Duralap Backend — Modular Monolith Architecture

Duralap Backend is a Kotlin-based Spring Boot **Modular Monolith** application for real-time messaging, chat, voice/video presence, notifications, media storage, analytics, and search.

---

## Architecture at a Glance

The backend is organized into **one deployable Spring Boot application**, **9 strongly encapsulated domain business modules**, and **5 shared technical libraries**:

```text
DuralapBackend/
├── app/                                       # Single executable Spring Boot application
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/com/example/duralap/
│       │   └── DuralapApplication.kt          # Application Entry Point
│       └── resources/
│           └── application.properties         # Unified Configuration
│
├── modules/                                   # Domain Business Modules
│   ├── auth/                                  # Authentication, JWT tokens, refresh tokens
│   ├── user/                                  # User profiles, status, user caching
│   ├── chat/                                  # Conversations & conversation requests
│   ├── message/                               # Message dispatch, ACKs, sync, and history
│   ├── presence/                              # Calls, WebRTC signaling relay, presence cache
│   ├── notification/                          # Real-time and persistent notifications
│   ├── media/                                 # Supabase media storage & signed URLs
│   ├── search/                                # Full-text cursor search across users & chats
│   └── analytics/                             # Platform aggregates & metrics
│
├── shared/                                    # Shared Technical Libraries
│   ├── shared-kernel/                         # DTOs, domain events, shared enums, exceptions
│   ├── shared-security/                       # Stateless JWT filter & security configuration
│   ├── shared-mongo/                          # Mongo client & compound index initializer
│   ├── shared-redis/                          # Redis template & sliding window rate limiter
│   └── shared-websocket/                      # Unified STOMP broker on port 8080 (/websocket)
│
├── Dockerfile                                 # Multi-stage single-container build
├── docker-compose.yml                         # Minimal deployment
├── run.sh                                     # Universal startup script
├── settings.gradle.kts                        # Modular Monolith Gradle settings
└── build.gradle.kts                           # Unified dependency & Kotlin compiler config
```

---

## Tech Stack

- **Language & Runtime**: Kotlin 2.0.21 on Java 21 (JVM)
- **Framework**: Spring Boot 3.4.1 (Web, Security, Data MongoDB, Data Redis, WebSocket/STOMP)
- **Database**: MongoDB Atlas (High-throughput Compound Indexes)
- **Cache & Real-time PubSub**: Upstash Redis (Sliding Window Rate Limiting, WebRTC Relay)
- **Object Storage**: Supabase Storage
- **Security**: Stateless HS512 JWT Authentication + Argon2 Password Hashing

---

## Quick Start: Choose Your Mode

Duralap Backend supports both **Local Native Execution (Without Docker)** and **Containerized Execution (With Docker)**.

### Option 1: Run WITHOUT Docker (Local JVM / Fastest for Development)

You can run the backend directly on your host machine using Gradle or the pre-built JAR:

```bash
# 1. Start directly via Universal Script
./run.sh --local

# OR start directly via Gradle
cd DuralapBackend
./gradlew :app:bootRun

# OR build and run the executable JAR
./run.sh --jar
```

- **Database / Cache**: By default, it connects to MongoDB Atlas & Upstash Redis automatically without requiring any local database installations.
- **Custom Configuration**: Copy `.env.example` to `.env` to configure your own MongoDB, Redis, or Supabase credentials.

---

### Option 2: Run WITH Docker (Full Container Stack)

If you prefer running inside Docker containers with local containerized Redis:

```bash
# 1. Start Docker Compose via Universal Script
./run.sh --docker

# OR directly with Docker Compose
cd DuralapBackend
docker compose up --build -d
```

- **Stop Containers**:
  ```bash
  ./run.sh stop
  # or: docker compose down
  ```

---

### Application Endpoints

Regardless of execution mode, the endpoints are mapped consistently:

| Service / Endpoint | URL | Description |
| :--- | :--- | :--- |
| **REST API Base** | `http://localhost:8080/api` | All domain module REST endpoints |
| **WebSocket & STOMP** | `ws://localhost:8080/websocket` | Real-time chat, typing indicators, signaling |
| **Actuator Health** | `http://localhost:8080/actuator/health` | Application healthcheck |
| **Actuator Metrics** | `http://localhost:8080/actuator/metrics` | Prometheus & JVM performance metrics |
| **LAN / Android Device** | `http://<YOUR_LOCAL_IP>:8080` | Real mobile device access on Wi-Fi |
| **Android Emulator** | `http://10.0.2.2:8080` | Local Android Studio emulator bridge |

---

## Universal CLI Script Reference (`run.sh`)

| Command | Action |
| :--- | :--- |
| `./run.sh` | Smart launcher (auto-detects Docker daemon or runs local JVM) |
| `./run.sh --local` | Runs Spring Boot locally on JVM without Docker (`:app:bootRun`) |
| `./run.sh --docker` | Builds image and starts Docker Compose stack (`docker compose up -d`) |
| `./run.sh --jar` | Builds and runs standalone executable JAR |
| `./run.sh --build` | Compiles all modules and packages `bootJar` |
| `./run.sh --test` | Runs JUnit test suite across all 14 modules (`./gradlew check`) |
| `./run.sh --status` | Checks HTTP `/actuator/health` status |
| `./run.sh --stop` | Stops Docker containers and background daemons |
| `./run.sh --help` | Displays interactive help menu |

---

## Local Development & Testing

### Build and Run Tests
```bash
./gradlew check
```

### Build Executable Application JAR
```bash
./gradlew :app:bootJar
```

### Run Locally
```bash
./gradlew :app:bootRun
```
