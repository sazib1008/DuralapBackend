# Duralap Modular Monolith Architecture Specification

## 1. Architectural Vision

The target architecture unifies the entire Duralap backend into:
* **One deployable Spring Boot application (`duralap.jar`)**
* **One JVM process**
* **One Gradle root project with strongly encapsulated module boundaries**
* **Zero internal HTTP / REST / Gateway hops**
* **Zero Kafka overhead for internal synchronous domain communications**
* **One unified WebSocket / STOMP endpoint (`/websocket`)**
* **Clean, interface-driven Public Module APIs & Spring Domain Events**

```
                              ┌─────────────────────────────────────────┐
                              │             Client Layer                │
                              │     Android App / Web Frontend / REST   │
                              └────────────────────┬────────────────────┘
                                                   │
                                                   ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       Duralap Modular Monolith Runtime (JVM)                                     │
│                                                                                                                 │
│   ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐   │
│   │                                       Web & Security Layer (Port 8080)                                  │   │
│   │       Spring Security (JWT + Argon2)   │   Unified STOMP WebSocket Broker (/websocket)   │   CORS/Filter   │   │
│   └────────────────────────────────────────────────────┬────────────────────────────────────────────────────┘   │
│                                                        │                                                        │
│   ┌────────────────────────────────────────────────────┴────────────────────────────────────────────────────┐   │
│   │                                            Business Modules                                             │   │
│   │                                                                                                         │   │
│   │   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐          │   │
│   │   │     Auth     │   │     User     │   │     Chat     │   │   Message    │   │   Presence   │          │   │
│   │   │    Module    │   │    Module    │   │    Module    │   │    Module    │   │    Module    │          │   │
│   │   └───────┬──────┘   └───────┬──────┘   └───────┬──────┘   └───────┬──────┘   └───────┬──────┘          │   │
│   │           │                  │                  │                  │                  │                 │   │
│   │   ┌───────┴──────┐   ┌───────┴──────┐   ┌───────┴──────┐   ┌───────┴──────┐   ┌───────┴──────┐          │   │
│   │   │ Notification │   │    Media     │   │    Search    │   │  Analytics   │   │ SharedKernel │          │   │
│   │   │    Module    │   │    Module    │   │    Module    │   │    Module    │   │ (Events/DTOs)│          │   │
│   │   └──────────────┘   └──────────────┘   └──────────────┘   └──────────────┘   └──────────────┘          │   │
│   │                                                                                                         │   │
│   │                  Inter-Module Communication: Public APIs / Kotlin Interfaces / Spring Events             │   │
│   └────────────────────────────────────────────────────┬────────────────────────────────────────────────────┘   │
│                                                        │                                                        │
│   ┌────────────────────────────────────────────────────┴────────────────────────────────────────────────────┐   │
│   │                                        Infrastructure & Persistence                                     │   │
│   │        Shared Mongo DB Config      │      Shared Redis Pool & PubSub      │      Supabase Client        │   │
│   └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────┬────────────────────────────────────────────────────────┘
                                                         │
                                   ┌─────────────────────┴─────────────────────┐
                                   ▼                                           ▼
                    ┌─────────────────────────────┐             ┌─────────────────────────────┐
                    │     MongoDB Atlas Cluster   │             │      Upstash Redis Cloud    │
                    │   (users, messages, chats,  │             │  (presence, signaling,      │
                    │     calls, notifications)   │             │   caches, rate-limiting)    │
                    └─────────────────────────────┘             └─────────────────────────────┘
```

---

## 2. Target Module Inventory & Boundaries

Each module strictly owns its entities, repositories, and business services. Cross-module access is performed **exclusively** through public module API interfaces or Spring Domain Application Events.

### 2.1 `modules:auth` (Authentication & Security Domain)
* **Responsibilities**: User registration, login authentication, token refresh, token revocation, Argon2 password encoding, authentication context validation.
* **Owned Entities**: `RefreshToken`, `User` (Credential ownership).
* **Owned Repositories**: `RefreshTokenRepository`.
* **Public API**: `AuthModuleApi`
* **Dependencies**: Depends on `UserModuleApi`, `ChatModuleApi` (for user conversation lookup), `shared:shared-kernel`, `shared:shared-security`.

### 2.2 `modules:user` (User Domain)
* **Responsibilities**: User profile management, username/email uniqueness validation, user status transitions (ONLINE/OFFLINE), user profile search, profile image link updates.
* **Owned Entities**: `User` (Profile & Status).
* **Owned Repositories**: `UserRepository`.
* **Public API**: `UserModuleApi`
  ```kotlin
  interface UserModuleApi {
      fun findUserById(id: String): UserResponse?
      fun findUserByUsername(username: String): UserResponse?
      fun findUserByEmail(email: String): UserResponse?
      fun findUsersByIds(ids: Set<String>): Map<String, UserResponse>
      fun existsById(id: String): Boolean
      fun existsByUsername(username: String): Boolean
      fun existsByEmail(email: String): Boolean
      fun updateUserStatus(id: String, status: UserStatus): UserResponse
      fun updateCallStatus(id: String, isInCall: Boolean, callId: String?): UserResponse
  }
  ```
* **Dependencies**: `shared:shared-kernel`, `shared:shared-redis`.

### 2.3 `modules:chat` (Conversation & Relationship Domain)
* **Responsibilities**: Conversation lifecycle, WhatsApp-like conversation requests (PENDING, ACCEPTED, REJECTED), participant management, conversation list retrieval, conversation seeding.
* **Owned Entities**: `Conversation`, `ConversationRequest`, `UserConversations`.
* **Owned Repositories**: `ConversationRepository`, `ConversationRequestRepository`, `UserConversationsRepository`.
* **Public API**: `ChatModuleApi`
  ```kotlin
  interface ChatModuleApi {
      fun getConversationById(id: String): ConversationResponse?
      fun isUserParticipant(conversationId: String, userId: String): Boolean
      fun getParticipantIds(conversationId: String): Set<String>
      fun isConversationAccepted(conversationId: String): Boolean
      fun updateLastMessage(conversationId: String, messageId: String, senderId: String, content: String, messageType: MessageType, timestamp: Instant)
      fun getUserConversationIds(userId: String): Set<String>
  }
  ```
* **Events Published**: `ConversationCreatedEvent`, `ConversationUpdatedEvent`.
* **Dependencies**: `UserModuleApi`, `MessageModuleApi` (for last message preview), `shared:shared-kernel`.

### 2.4 `modules:message` (Messaging & Synchronization Domain)
* **Responsibilities**: Message persistence, duplicate prevention (UUID deduplication), message pagination, CQRS querying, missed message synchronization, delivery/read receipts (ACKs).
* **Owned Entities**: `Message`.
* **Owned Repositories**: `MessageRepository`.
* **Public API**: `MessageModuleApi`
  ```kotlin
  interface MessageModuleApi {
      fun sendMessage(request: MessageCreateRequest): MessageResponse
      fun getMessageById(id: String): MessageResponse?
      fun getLastMessageForConversation(conversationId: String): MessageResponse?
      fun countUnreadMessages(conversationId: String, userId: String): Long
      fun deleteMessagesByConversationId(conversationId: String)
  }
  ```
* **Events Published**: `MessageCreatedEvent`, `MessageStatusUpdatedEvent`.
* **Dependencies**: `UserModuleApi`, `ChatModuleApi`, `shared:shared-kernel`, `shared:shared-redis`.

### 2.5 `modules:presence` (Real-Time Presence & WebRTC Calling Domain)
* **Responsibilities**: Online/offline presence management, call signaling relay (SDP Offer/Answer/ICE), call history, missed/active call tracking.
* **Owned Entities**: `Call`.
* **Owned Repositories**: `CallRepository`.
* **Public API**: `PresenceModuleApi`
* **Dependencies**: `UserModuleApi`, `shared:shared-kernel`, `shared:shared-redis`.

### 2.6 `modules:notification` (In-App Notification Domain)
* **Responsibilities**: Notification persistence, unread counts, mark as read, STOMP push dispatching.
* **Owned Entities**: `Notification`.
* **Owned Repositories**: `NotificationRepository`.
* **Public API**: `NotificationModuleApi`
* **Event Listeners**: Listens to `MessageCreatedEvent`, `ConversationCreatedEvent`.
* **Dependencies**: `UserModuleApi`, `shared:shared-kernel`.

### 2.7 `modules:media` (Storage & File Management Domain)
* **Responsibilities**: File upload validation, MIME validation, storage size quota enforcement, Supabase S3-compatible integration, signed download URL generation, file deletion.
* **Owned Entities**: `MediaFile`.
* **Owned Repositories**: `MediaFileRepository`.
* **Public API**: `MediaModuleApi`
* **Dependencies**: `shared:shared-kernel`, Supabase Client.

### 2.8 `modules:search` (Search & Discovery Domain)
* **Responsibilities**: Multi-criteria regex/text search across users and conversations with cursor-based pagination.
* **Dependencies**: `UserModuleApi`, `ChatModuleApi`, `shared:shared-kernel`, MongoDB MongoTemplate.

### 2.9 `modules:analytics` (Telemetry & Reporting Domain)
* **Responsibilities**: Platform KPIs, aggregate counters, event telemetry.
* **Owned Entities**: `AnalyticsEvent`.
* **Owned Repositories**: `AnalyticsEventRepository`.
* **Event Listeners**: Listens to `MessageCreatedEvent`, `ConversationCreatedEvent`.
* **Dependencies**: `shared:shared-kernel`.

---

## 3. Inter-Module Communication Rules

1. **No Direct Database Access**: No module may inject or access another module's `Repository` or MongoDB collection directly.
2. **Explicit Module APIs**: Inter-module synchronous queries must go through the receiving module's `@Service` implementing a public `interface` in `api/`.
3. **Internal Domain Events for Side Effects**: Cross-cutting actions (e.g. notifications on message create, analytics logging, conversation list refresh) must use Spring's `ApplicationEventPublisher` and `@EventListener` (with `@Async` where non-blocking execution is preferred).
4. **No Internal HTTP or RestTemplate Calls**: All internal communications are native in-memory Kotlin method calls.
5. **No Circular Dependencies**: Dependencies flow in a strict directed acyclic graph (DAG).

---

## 4. Real-Time WebSocket Architecture

A single centralized WebSocket broker configuration in `shared:shared-websocket` exposes `/websocket`:

* Interceptor validates JWT from STOMP header on `CONNECT` and updates presence in `UserPresenceCache`.
* STOMP MessageBroker routes:
  * `/topic/conversation/{conversationId}`: Real-time chat messages and status updates for active conversation screens.
  * `/user/{userId}/queue/messages`: Direct delivery to recipient's private inbox.
  * `/user/{userId}/queue/conversations`: Conversation list updates with latest preview and unread counters.
  * `/user/{userId}/queue/message-status`: Real-time delivery and read receipt ticks.
  * `/user/{userId}/queue/notifications`: Instant in-app notification toasts.
  * `/topic/user/{userId}/signaling`: WebRTC video/audio call signaling.

---

## 5. Security & Authentication Architecture

* Centralized Spring Security Filter Chain in `shared:shared-security`.
* Stateless JWT authentication with Bearer tokens.
* Argon2 password hashing.
* Unified endpoints matching client expectations:
  * `/api/auth/**` -> Public
  * `/api/users/check-username/**`, `/api/users/check-email/**` -> Public
  * `/websocket/**` -> Handled by STOMP interceptor
  * `/actuator/**` -> Health checks
  * `/api/**` -> Authenticated
