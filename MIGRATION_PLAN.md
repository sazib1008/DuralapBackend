# Duralap Microservices to Modular Monolith Migration Plan

## 1. Objectives

1. Consolidate 10 microservices and 8 common subprojects into **1 unified Spring Boot application (`app`)** with **9 distinct business modules** and **5 focused shared libraries**.
2. Replace all internal Kafka RPC and HTTP proxy routing with **in-memory Kotlin method calls via typed Public Module APIs** and **Spring Domain Application Events**.
3. Decommission `gateway-service`, inter-service proxying, and redundant microservice containers.
4. Merge 3 separate WebSocket configurations into **1 unified WebSocket STOMP endpoint (`/websocket`)**.
5. Ensure 100% backward compatibility for all external REST APIs, WebSocket destinations, MongoDB Atlas collections, and Redis structures.
6. Achieve a clean build and passing test suite with `./gradlew clean build test`.

---

## 2. Target Project Layout

```
DuralapBackend/
│
├── app/                                       # Single Spring Boot Application Entry Point
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/com/example/duralap/
│       │   └── DuralapApplication.kt
│       └── resources/
│           ├── application.properties
│           └── application.yml
│
├── modules/                                   # Strongly Encapsulated Business Modules
│   ├── auth/                                  # Authentication, JWT, Refresh Tokens
│   ├── user/                                  # User Profiles, Status, Search
│   ├── chat/                                  # Conversations, Requests, Members
│   ├── message/                               # Messages, CQRS Query/Command, Sync, ACKs
│   ├── presence/                              # Online Status, WebRTC Calls & Signaling
│   ├── notification/                          # In-app Notifications & WebSocket Toasts
│   ├── media/                                 # Supabase Media Uploads & Signed URLs
│   ├── search/                                # Multi-entity Regex/Text Search
│   └── analytics/                             # Telemetry & KPI Aggregates
│
├── shared/                                    # Reusable Foundation Libraries
│   ├── shared-kernel/                         # DTOs, Domain Events, Enums, Exceptions
│   ├── shared-security/                       # Spring Security, JWT Auth Filter, Argon2
│   ├── shared-mongo/                          # Mongo Client & Index Lifecycle
│   ├── shared-redis/                          # Redis Pools, Rate Limiting Filter/Service
│   └── shared-websocket/                      # WebSocket STOMP Broker & JWT Interceptor
│
├── Dockerfile                                 # Single JVM Container Build
├── docker-compose.yml                         # Application + Redis + MongoDB Atlas
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 3. Incremental Execution Roadmap

### Stage 1: Shared Modules Foundation
* Create `shared/shared-kernel` containing all DTOs, Event records (`MessageCreatedEvent`, `ConversationCreatedEvent`, etc.), models, and exceptions.
* Create `shared/shared-security` containing `SecurityConfig`, `JwtTokenProvider`, `JwtAuthenticationFilter`, and `AuthenticatedUserUtil`.
* Create `shared/shared-mongo` containing `MongoConfig` and `MongoIndexConfig`.
* Create `shared/shared-redis` containing Redis templates, `RateLimitingService`, and `RateLimitingFilter`.
* Create `shared/shared-websocket` containing the unified `WebSocketConfig` and authentication interceptor.

### Stage 2: Core Domain Modules (User & Auth)
* Implement `modules/user` with `User` entity, `UserRepository`, `UserService`, `UserCache`, `UserController`, and public `UserModuleApi`.
* Implement `modules/auth` with `RefreshToken` entity, `RefreshTokenRepository`, `AuthService`, `RefreshTokenService`, `AuthController`, and `AuthModuleApi`.
* Verify compilation of user and auth modules.

### Stage 3: Communication Modules (Chat, Message, Presence)
* Implement `modules/chat` with `Conversation`, `ConversationRequest`, `UserConversations` repositories, `ConversationService`, `ConversationRequestService`, controllers, `ChatModuleApi`, and Spring Event publisher/listeners.
* Implement `modules/message` with `Message` repository, `MessageService`, `MessageController`, `MessageAckController`, `MessageModuleApi`, and real-time WebSocket dispatcher listening to Spring domain events.
* Implement `modules/presence` with `Call` repository, `UserPresenceCache`, `CallSignalingService`, `WebRtcSignalingSubscriber`, `CallService`, and `CallController`.
* Verify compilation and tests.

### Stage 4: Supporting Modules (Media, Notification, Search, Analytics)
* Implement `modules/media` with `MediaFileRepository`, `SupabaseStorageService`, `MediaService`, and `MediaController`.
* Implement `modules/notification` with `NotificationRepository`, `NotificationService`, `NotificationController`, and `NotificationEventListener` listening to Spring domain events.
* Implement `modules/search` with `SearchService` and `SearchController` invoking `UserModuleApi` and `ChatModuleApi`.
* Implement `modules/analytics` with `AnalyticsEventRepository`, `AnalyticsService`, `AnalyticsController`, and `AnalyticsEventListener`.

### Stage 5: Main Application Assembly & Configuration
* Implement `app` with `DuralapApplication.kt` scanning `@ComponentScan(basePackages = ["com.example.duralap"])` and `@EnableMongoRepositories`.
* Create centralized `application.properties` with environment defaults and production-grade connection pooling for Mongo, Redis, and Tomcat.
* Decommission `gateway-service` and obsolete microservice folders.
* Reconfigure `settings.gradle.kts` and root `build.gradle.kts`.

### Stage 6: Verification, Testing & Docker Optimization
* Update test suites in `modules/auth` and `modules/message`.
* Run `./gradlew clean build test` to verify full compilation and test execution.
* Update `Dockerfile` and `docker-compose.yml` for the single monolith deployment.
