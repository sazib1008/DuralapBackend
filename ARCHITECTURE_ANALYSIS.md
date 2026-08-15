# Duralap System Architecture Analysis (Pre-Migration)

## 1. Executive Summary

**Duralap** is a real-time messaging, calling (WebRTC), social, and media platform built with Kotlin and Spring Boot 3.4.1 (JVM 21). Prior to migration, the backend was split into **10 microservices** plus a shared `common` module, communicating over a complex mesh of HTTP REST proxies (via an ad-hoc gateway), Kafka event topics, Redis caches/PubSub, and a shared MongoDB Atlas cluster.

This document presents a comprehensive, empirical audit of the existing microservice architecture, including dependency graphs, data ownership, event flows, Redis usage, and architectural anti-patterns.

---

## 2. Existing Microservices Inventory

| Service | Port | Primary Responsibility | Data Store / State | Inter-Service & External Dependencies |
| :--- | :--- | :--- | :--- | :--- |
| **`gateway-service`** | 8080 | Reverse proxy router for client HTTP requests | None | Forwards all `/api/{service}/**` to microservices 8081–8089 via RestTemplate |
| **`auth-service`** | 8081 | Authentication, JWT issuing, Argon2 password hashing, refresh tokens | MongoDB (`users`, `refresh_tokens`, `conversations`, `user_conversations`) | MongoDB, Redis (rate limit), JWT |
| **`user-service`** | 8082 | User profile management, status updates, search | MongoDB (`users`), Redis (`user:cache:*`) | MongoDB, Redis |
| **`chat-service`** | 8083 | Conversation lifecycle, conversation requests (accept/reject), participant management | MongoDB (`conversations`, `conversation_requests`, `messages`, `users`, `user_conversations`) | MongoDB, Kafka (`chat.events.conversation.created`), Redis (rate limiting), WebSocket |
| **`message-service`** | 8084 | Message sending, message sync, read/delivery receipts (ACKs) | MongoDB (`messages`, `conversations`, `users`), Redis (`conversation:*:participants`, `user:*:presence`) | MongoDB, Kafka (`chat.events.message.created`, `chat.events.message.status`), Redis, WebSocket |
| **`media-service`** | 8085 | Multipart media upload, download URLs, size validation | MongoDB (`media_files`), Supabase Storage Bucket | MongoDB, Supabase Storage REST API, Kafka (`media-uploaded`, `media-deleted`) |
| **`presence-service`** | 8086 | Call initiation, WebRTC SDP/ICE signaling relay, user online presence | MongoDB (`calls`, `users`), Redis (`user:*:presence`, `rtc:signal:user:*` PubSub) | MongoDB, Redis (PubSub & cache), WebSocket |
| **`notification-service`** | 8087 | In-app notifications persistence and WebSocket push | MongoDB (`notifications`, `users`, `conversations`) | MongoDB, Kafka (`message-created`, `conversation-created`), WebSocket |
| **`analytics-service`** | 8088 | Aggregate platform metrics, event recording | MongoDB (`analytics_events`, `users`, `messages`, `conversations`, `calls`) | MongoDB, Kafka (`message-created`, `conversation-created`) |
| **`search-service`** | 8089 | Paginated cursor search for users and conversations | MongoDB (`users`, `conversations`) | MongoDB (MongoTemplate regex queries) |

---

## 3. Common Modules Inventory

The `common/` directory contains 8 submodules:

* `common:common-dto`: DTOs (`UserResponse`, `MessageDto`, `ConversationDto`, `CallDto`, etc.) and shared enums.
* `common:common-events`: Kafka event definitions (`MessageCreatedEvent`, `ConversationCreatedEvent`, `MessageStatusUpdatedEvent`, `MediaUploadedEvent`, `MediaDeletedEvent`).
* `common:common-mongo`: Shared MongoDB config, index configs, and Spring Data MongoDB entities & repositories (`UserRepository`, `MessageRepository`, `ConversationRepository`, `CallRepository`, etc.).
* `common:common-redis`: Shared Redis rate limiting filter and service.
* `common:common-security`: Centralized Spring Security filter chain, JWT token provider, Argon2 encoder, and security context helpers.
* `common:common-utils`: Global exception handlers and error response records.
* `common:common-kafka`: Kafka producer/consumer JSON serializers configuration.
* `common:common-websocket`: Empty placeholder module with WebSocket starter dependency.

---

## 4. Current Communication & Dependency Graph

### Client-to-Backend Flow
```
Client (Android/Web)
       │
       ▼ [HTTP Port 8080]
┌─────────────────────────────────────────────────────────────┐
│                       gateway-service                       │
│    (Manual RestTemplate reverse proxy to localhost:808X)    │
└────────┬────────┬────────┬────────┬────────┬────────┬───────┘
         │        │        │        │        │        │
         ▼        ▼        ▼        ▼        ▼        ▼
       auth     users    chat    messages  media   presence / etc.
       8081     8082     8083      8084    8085      8086
```

### Inter-Service Communication Flow
```
[Client] ──POST /api/messages──► [message-service]
                                       │
                    ┌──────────────────┴──────────────────┐
                    ▼ (Save DB)                           ▼ (Kafka Publish)
             MongoDB: messages               Topic: chat.events.message.created
                                                          │
                                     ┌────────────────────┼────────────────────┐
                                     ▼                    ▼                    ▼
                           [message-service]   [notification-service]  [analytics-service]
                          (MessageEventConsumer)  (KafkaListener)        (KafkaListener)
                                     │                    │                    │
                                     ▼                    ▼                    ▼
                               STOMP WS Push        Create Notification   Save Analytics
```

---

## 5. Database & Entity Ownership Analysis

Currently, MongoDB collections are shared indiscriminately across all microservices because entities and repositories were placed in `common:common-mongo`:

| Collection | Entities | Accessed By Services | Domain Boundary Violation? |
| :--- | :--- | :--- | :--- |
| `users` | `User` | `auth`, `user`, `chat`, `message`, `media`, `presence`, `notification`, `analytics`, `search` | **Severe Violation**: Almost every service directly queries `UserRepository`. |
| `conversations` | `Conversation` | `auth`, `chat`, `message`, `notification`, `analytics`, `search` | **Violation**: `message-service` and `auth-service` directly read/write `ConversationRepository`. |
| `conversation_requests` | `ConversationRequest` | `chat` | Owned by Chat. |
| `messages` | `Message` | `chat`, `message`, `analytics` | **Violation**: `chat-service` queries `MessageRepository` directly for seeding and last message. |
| `calls` | `Call` | `presence`, `analytics` | Owned by Presence/Call. |
| `media_files` | `MediaFile` | `media` | Owned by Media. |
| `notifications` | `Notification` | `notification` | Owned by Notification. |
| `refresh_tokens` | `RefreshToken` | `auth` | Owned by Auth. |
| `analytics_events` | `AnalyticsEvent` | `analytics` | Owned by Analytics. |
| `user_conversations` | `UserConversations` | `auth`, `chat` | Shared mapping collection. |

---

## 6. Kafka Topic Inventory

| Topic Name | Producer(s) | Consumer(s) | Original Intent | Target Monolith Status |
| :--- | :--- | :--- | :--- | :--- |
| `chat.events.conversation.created` | `chat-service` | `chat-service` | Dispatch STOMP event to `/user/{id}/queue/conversations` | **REPLACE with Internal Spring Domain Event** |
| `chat.events.message.created` | `message-service` | `message-service` | Dispatch STOMP event to `/topic/conversation/{id}` & `/user/{id}/queue/messages` | **REPLACE with Internal Spring Domain Event** |
| `chat.events.message.status` | `message-service` | `message-service` | Dispatch STOMP message status updates (DELIVERED/READ) | **REPLACE with Internal Spring Domain Event** |
| `message-created` | None (misconfigured name) | `notification-service`, `analytics-service` | Trigger notification generation and analytics counters | **REPLACE with Internal Spring Domain Event** |
| `conversation-created` | None (misconfigured name) | `notification-service`, `analytics-service` | Trigger conversation notifications & metrics | **REPLACE with Internal Spring Domain Event** |
| `media-uploaded` | `media-service` | None | Media upload notification event | **REPLACE with Internal Domain Event** (or retain for external async hooks) |
| `media-deleted` | `media-service` | None | Media deletion audit event | **REPLACE with Internal Domain Event** |

---

## 7. Redis Usage Inventory

| Redis Feature / Key Pattern | Responsible Component | Purpose | Retain in Modular Monolith? |
| :--- | :--- | :--- | :--- |
| `user:{userId}:presence` | Presence / Message / WebSocket | String key with 2m TTL tracking online status | **YES** (Centralized Presence Cache) |
| `user:cache:{userId}`, `user:cache:{username}:by-username` | User Module | User profile JSON cache with 30m TTL | **YES** (User Cache) |
| `conversation:{convId}:participants` | Message Module | Redis Set caching participant IDs for 1 hr | **YES** (Message Validation Cache) |
| `rate:limit:requests:{userId}`, `rate:limit:messages:{userId}` | Common Redis / Chat / Message | Redis ZSet sliding window rate limiter | **YES** (Rate Limiting) |
| `rtc:signal:user:{userId}` | Presence / WebRTC Signaling | Redis Pub/Sub channel for distributing SDP/ICE signals | **YES** (WebRTC Signaling Bus) |

---

## 8. WebSocket & STOMP Architecture

The WebSocket connections are authenticated via JWT in the STOMP `CONNECT` header frame.
Client-facing destinations:

* `/topic/conversation/{conversationId}`: Real-time broadcast for active chat window.
* `/user/{userId}/queue/messages`: Direct delivery to recipient's private queue when online.
* `/user/{userId}/queue/conversations`: Conversation list updates & unread counts.
* `/user/{userId}/queue/message-status`: Status transitions (`SENT` -> `DELIVERED` -> `READ`).
* `/user/{userId}/queue/notifications`: Real-time in-app notifications.
* `/topic/user/{userId}/signaling`: WebRTC call signaling (SDP Offer, Answer, ICE candidates).
* STOMP inbound destinations:
  * `/app/chat.ack.delivery`: Acknowledge message delivery.
  * `/app/chat.ack.read`: Acknowledge message read.

In the microservices version, multiple services (`chat-service`, `presence-service`, `notification-service`) each attempted to bind WebSocket brokers independently on port 8083, 8086, 8087, creating severe port and connection fragmentation for mobile clients.

---

## 9. Architectural Flaws & Anti-Patterns Identified

1. **Distributed Monolith disguised as Microservices**: All services shared a single database cluster (`cluster0.lsfwn33.mongodb.net`) and directly imported shared repositories from `common-mongo`.
2. **Gateway Bottleneck & Unnecessary Network Overhead**: `gateway-service` was a simple blocking `RestTemplate` proxy converting HTTP requests to downstream HTTP calls, adding latency, connection pool pressure, and serialization overhead.
3. **Kafka Used for Same-Service Internal Communication**: `chat-service` and `message-service` published to Kafka only to immediately consume from Kafka inside the same JVM instance.
4. **Mismatched Kafka Topics**: `notification-service` listened to `message-created` while `message-service` published to `chat.events.message.created`, causing event drops across boundaries.
5. **WebSocket Fragmentation**: 3 separate microservices exposed WebSocket endpoints on different ports, forcing clients to maintain multiple persistent socket connections.
6. **Violations of Domain Encapsulation**: Services directly queried other services' MongoDB repositories rather than calling domain APIs or listening to domain events.
7. **Massive Docker Resource Overhead**: 10 separate Spring Boot JVM processes + Zookeeper + Kafka + Redis required >8 GB RAM to run locally.

---

## 10. Conclusion

The codebase is an ideal candidate for a **Modular Monolith**. Business logic is already well structured in Kotlin, but artificial microservice boundaries and shared database dependencies caused unnecessary operational complexity and performance degradation. Moving to a single Spring Boot application with strong module boundaries will eliminate all proxy latency, eliminate Kafka overhead for internal flows, consolidate WebSockets to a single endpoint, and provide strict compile-time domain encapsulation.
