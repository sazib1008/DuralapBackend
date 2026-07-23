# Chapter 1 - Vision & Philosophy

---

# Duralap Engineering Bible

**Project Name:** Duralap

**Version:** 1.0

**Document Status:** Living Document

**Project Type:** Enterprise-Grade Real-Time Messaging Platform

---

# 1.1 Vision

Duralap is an enterprise-grade, production-ready real-time messaging platform designed to demonstrate modern software engineering principles, distributed system architecture, and scalable backend development.

The goal of Duralap is **not** to clone WhatsApp, Telegram, Messenger, or Discord.

Instead, Duralap combines the strongest ideas from these platforms while applying enterprise-level engineering practices to create a maintainable, scalable, secure, and extensible messaging system.

This project is intended to be a long-term engineering project rather than a short-term academic assignment.

---

# 1.2 Mission

The mission of Duralap is to build a messaging platform that serves as:

- A production-ready backend reference implementation.
- A comprehensive learning platform for modern backend engineering.
- A high-quality portfolio project.
- A final year university project.
- A foundation for future real-world applications.

Every architectural decision must prioritize long-term maintainability over short-term development speed.

---

# 1.3 Philosophy

Duralap follows one simple philosophy:

> **Build software as if millions of users will eventually use it.**

This philosophy affects every engineering decision.

Examples include:

- Clean Architecture instead of tightly coupled code.
- Event-driven communication instead of direct dependencies.
- Strong documentation instead of undocumented assumptions.
- Automated testing instead of manual verification.
- Observability instead of debugging through log statements.
- Security by design instead of security as an afterthought.

---

# 1.4 Core Engineering Principles

Every feature developed for Duralap must satisfy the following principles.

## Simplicity

Prefer simple solutions whenever they provide sufficient scalability and maintainability.

Avoid unnecessary complexity.

---

## Maintainability

Code should be understandable by future developers.

A feature that works today but becomes impossible to maintain tomorrow is considered a failure.

---

## Scalability

Every component should be designed with future growth in mind.

Although the initial deployment may serve only a few users, the architecture should allow future expansion without complete redesign.

---

## Reliability

System behavior should be predictable.

Unexpected failures should be minimized through validation, testing, monitoring, and proper error handling.

---

## Security

Security is not an optional feature.

Authentication, authorization, validation, encryption, logging, and auditing are fundamental parts of every feature.

---

## Performance

Optimize only where necessary.

Avoid premature optimization.

However, avoid known inefficient designs.

---

## Testability

Every business feature should be independently testable.

Testing is considered part of the implementation rather than an optional activity.

---

## Documentation

If architecture exists only in the developer's memory, it does not exist.

Every significant engineering decision should be documented.

---

# 1.5 Long-Term Goals

Duralap is designed as a continuously evolving platform.

The roadmap includes:

### Phase 1

- Authentication
- User Management
- Private Messaging

### Phase 2

- Media Sharing
- Groups
- Notifications

### Phase 3

- Search
- Monitoring
- Performance Optimization

### Phase 4

- Voice Calling
- Video Calling

### Phase 5

- Channels
- Communities

### Phase 6

- End-to-End Encryption
- Multi-Device Synchronization
- AI Features
- Global Deployment

---

# 1.6 Design Philosophy

The backend must remain independent of frontend technologies.

The backend should never contain assumptions specific to Android, iOS, Web, or Desktop.

Instead, every client consumes the same APIs and the same WebSocket protocol.

Supported platforms include:

- Android
- Web
- iOS
- Desktop
- Tablet

Future clients should require no backend redesign.

---

# 1.7 Architectural Philosophy

Duralap follows a Feature-Based Modular Monolith architecture.

The system is intentionally designed to allow future migration to Microservices.

Current architecture prioritizes:

- Simplicity
- Developer productivity
- Lower operational complexity

Future scalability is enabled through:

- Module boundaries
- Domain events
- Kafka integration
- Outbox pattern
- Independent business modules

---

# 1.8 Technology Philosophy

Technology choices are driven by engineering requirements rather than popularity.

The selected stack balances productivity, scalability, and maintainability.

| Technology | Purpose |
|------------|---------|
| Kotlin | Primary language |
| Spring Boot | Backend framework |
| MongoDB | Primary database |
| Redis | Cache & Presence |
| Kafka | Event Streaming |
| Supabase Storage | Media Storage |
| WebSocket + STOMP | Real-time Communication |
| Docker | Containerization |
| Prometheus | Metrics |
| Grafana | Monitoring |

Each technology has a clearly defined responsibility.

Technology overlap should be avoided.

---

# 1.9 Development Philosophy

The following order must always be followed.

```
Requirements

↓

Architecture

↓

Database Design

↓

API Design

↓

Implementation

↓

Testing

↓

Deployment

↓

Monitoring

↓

Optimization
```

Implementation must never begin before architecture is sufficiently defined.

---

# 1.10 Engineering Standards

Every feature must satisfy the following checklist before being considered complete.

- Correct Architecture
- Business Rules Implemented
- Security Reviewed
- Validation Added
- Authorization Verified
- Tests Written
- Documentation Updated
- Logging Implemented
- Metrics Added
- Performance Considered
- OpenAPI Updated

Incomplete features must not be merged.

---

# 1.11 Code Philosophy

The project follows the principle:

> **Code is read far more often than it is written.**

Therefore:

- Readability is preferred over cleverness.
- Explicit code is preferred over implicit behavior.
- Small classes are preferred over large classes.
- Clear names are preferred over abbreviations.
- Composition is preferred over inheritance.
- Immutability is preferred whenever practical.

---

# 1.12 Project Values

Duralap values:

- Quality
- Simplicity
- Transparency
- Documentation
- Security
- Scalability
- Maintainability
- Continuous Learning

These values should guide every engineering decision.

---

# 1.13 Definition of Success

The success of Duralap is **not** measured by the number of implemented features.

Instead, success is measured by:

- Architectural quality.
- Code quality.
- Reliability.
- Maintainability.
- Documentation quality.
- Testing quality.
- Deployment readiness.
- Production readiness.

A smaller, well-engineered system is preferred over a larger, poorly engineered one.

---

# 1.14 Guiding Principle

The guiding principle of Duralap is:

> **"Architecture First. Engineering Always."**

Every feature, module, API, and infrastructure component should contribute toward building a messaging platform that could confidently evolve into a production system serving real users.

This principle defines the engineering culture of the Duralap project.




# Chapter 2 - Business Requirements

---

# Duralap Engineering Bible

## Chapter 2

**Business Requirements**

Version: 1.0

Status: Approved

---

# 2.1 Purpose

The purpose of Duralap is to provide a modern, secure, scalable, and production-ready messaging platform that supports seamless communication between users across multiple devices and platforms.

The system must allow users to exchange messages, media, and information in real time while maintaining high standards of security, reliability, and performance.

---

# 2.2 Business Objectives

The primary business objectives are:

- Build a production-ready messaging platform.
- Support real-time communication.
- Support millions of future users without architectural redesign.
- Provide a clean and intuitive messaging experience.
- Demonstrate enterprise software engineering practices.
- Serve as a professional portfolio project.
- Serve as a university final year project.
- Act as a foundation for future commercial development.

---

# 2.3 Target Users

The application is designed for:

### Individual Users

People communicating privately with friends, family, or colleagues.

---

### Groups

Communities communicating within shared conversations.

---

### Organizations (Future)

Businesses using Duralap as an internal communication platform.

---

### Developers

Developers studying enterprise backend architecture and distributed systems.

---

# 2.4 Supported Platforms

The backend must support all client platforms equally.

Supported platforms include:

- Android
- Web
- iOS
- Desktop
- Tablet

No platform should receive backend-specific behavior.

---

# 2.5 Business Scope

The initial release includes:

- User Registration
- Authentication
- User Profiles
- Private Messaging
- Group Messaging
- Media Sharing
- Notifications
- Search
- User Privacy
- Device Management

Future releases include:

- Voice Calling
- Video Calling
- Channels
- Communities
- Bots
- AI Features
- End-to-End Encryption

---

# 2.6 Business Rules

## BR-001

Every user must have a unique username.

Usernames cannot be changed after registration.

---

## BR-002

Every account must be associated with one verified email address.

Email verification is mandatory before accessing the application.

---

## BR-003

Users may log in using:

- Email
- Username

Both methods authenticate the same account.

---

## BR-004

Passwords must satisfy complexity requirements.

Minimum:

8 characters

Maximum:

16 characters

Must contain:

- Uppercase
- Lowercase
- Number
- Special Character

---

## BR-005

A user may be logged in on multiple devices simultaneously.

Each device maintains an independent authenticated session.

---

## BR-006

Users may log out from:

- Current device
- All devices

---

## BR-007

Users may block other users.

Blocked users cannot:

- Send messages
- View online status
- Start conversations

---

## BR-008

Users control email discoverability.

Default:

Allow Discover by Email = ON

Users may disable this option.

Username remains discoverable.

---

## BR-009

User search supports:

- Username
- Email (if allowed)

Future:

- QR Code
- Share Link

---

## BR-010

Private conversations exist between exactly two participants.

Only one active private conversation may exist between the same pair of users.

---

## BR-011

Group conversations support multiple members.

Group ownership and permissions determine administrative actions.

---

## BR-012

Users may send:

- Text
- Images
- Videos
- Audio
- Voice Notes
- Documents
- GIFs
- Stickers
- Contacts
- Locations

Future:

- Polls
- Events
- Live Locations

---

## BR-013

Messages support:

- Reply
- Edit
- Delete For Me
- Delete For Everyone
- Forward
- Reactions

---

## BR-014

Messages may be edited within 15 minutes.

After that period editing is no longer allowed.

---

## BR-015

Deleted messages remain auditable internally when required by system policies.

Visible content is removed from user interfaces.

---

## BR-016

The system must maintain message ordering inside every conversation.

Ordering is based on server-assigned sequence numbers.

---

## BR-017

Every message has a delivery lifecycle.

States include:

- Sending
- Sent
- Delivered
- Read

---

## BR-018

Typing indicators are temporary.

Typing state automatically expires after inactivity.

---

## BR-019

Online presence is temporary.

Presence information is stored in Redis.

MongoDB is not used for presence tracking.

---

## BR-020

Media files are stored separately from message data.

Only metadata is stored in MongoDB.

---

# 2.7 Privacy Requirements

Users control:

- Profile visibility
- Email discoverability
- Last seen visibility (Future)
- Online status visibility (Future)
- Read receipts (Future)

Privacy settings apply immediately after update.

---

# 2.8 Availability Requirements

The application should remain available during normal operations.

Future deployments should support:

- Rolling updates
- Zero downtime deployments
- High availability

---

# 2.9 Performance Requirements

The system should:

- Authenticate users quickly.
- Deliver messages in real time.
- Maintain low latency.
- Handle high concurrent connections.
- Support horizontal scaling in the future.

Performance optimizations must not compromise correctness.

---

# 2.10 Reliability Requirements

The system should ensure:

- Message durability
- Reliable delivery
- Consistent ordering
- Safe retries
- Event replay support

Temporary infrastructure failures should not cause permanent data loss.

---

# 2.11 Security Requirements

Every business feature must include:

- Authentication
- Authorization
- Validation
- Audit Logging

Sensitive information must never be exposed to unauthorized users.

---

# 2.12 Business Constraints

The project uses:

Backend

- Spring Boot
- Kotlin

Database

- MongoDB

Cache

- Redis

Message Broker

- Kafka

Storage

- Supabase Storage

Real-time

- WebSocket + STOMP

These technologies define the implementation boundaries.

---

# 2.13 Assumptions

The following assumptions are made:

- Users have internet connectivity.
- Users possess verified email addresses.
- Mobile and web clients use the same backend.
- All communication occurs over HTTPS/WSS.
- Media uploads use signed URLs.

---

# 2.14 Success Criteria

The business requirements are considered fulfilled when:

- Users can securely register and authenticate.
- Users can discover each other.
- Private messaging functions correctly.
- Group messaging functions correctly.
- Media sharing operates reliably.
- Real-time communication is stable.
- Security requirements are satisfied.
- System architecture remains maintainable.

---

# 2.15 Future Business Expansion

Future releases may introduce:

- Voice Calls
- Video Calls
- Channels
- Communities
- Business Accounts
- AI Assistant
- Message Translation
- Scheduled Messages
- Cloud Backup
- End-to-End Encryption
- Multi-Region Deployment

These features should integrate without requiring major architectural redesign.

---

# 2.16 Chapter Summary

This chapter defines the business goals, functional boundaries, business rules, user expectations, and long-term vision of Duralap.

Subsequent chapters translate these business requirements into technical architecture, database design, APIs, security mechanisms, and implementation strategies while preserving the principles established here.


# Chapter 3 - Functional Requirements

---

# Duralap Engineering Bible

## Chapter 3

**Functional Requirements**

Version: 1.0

Status: Approved

---

# 3.1 Purpose

This chapter defines every functional capability that the Duralap platform must provide.

Each requirement is uniquely identified for traceability throughout development, testing, and maintenance.

Requirement IDs follow the format:

FR-<MODULE>-<NUMBER>

Example:

FR-AUTH-001

FR-MSG-012

FR-GROUP-005

---

# 3.2 Functional Modules

The system consists of the following functional modules.

FR-AUTH

Authentication

FR-USER

User Management

FR-CONV

Conversation

FR-MSG

Messaging

FR-GROUP

Groups

FR-MEDIA

Media

FR-NOTI

Notifications

FR-SEARCH

Search

FR-PRESENCE

Presence

FR-DEVICE

Device Management

FR-ADMIN (Future)

Administration

---

# 3.3 Authentication Module

## FR-AUTH-001

The system shall allow user registration using email and password.

Priority

Critical

---

## FR-AUTH-002

The system shall verify email ownership using OTP before account activation.

Priority

Critical

---

## FR-AUTH-003

The system shall allow login using:

• Email
• Username

Priority

Critical

---

## FR-AUTH-004

The system shall support Google OAuth2 authentication.

Priority

High

---

## FR-AUTH-005

The system shall issue:

Access Token

Refresh Token

after successful authentication.

Priority

Critical

---

## FR-AUTH-006

The system shall rotate refresh tokens.

Priority

Critical

---

## FR-AUTH-007

The system shall allow logout from:

• Current Device
• All Devices

Priority

High

---

## FR-AUTH-008

The system shall support password reset through email verification.

Priority

High

---

## FR-AUTH-009

The system shall lock accounts temporarily after excessive failed login attempts.

Priority

Medium

---

# 3.4 User Module

## FR-USER-001

Users shall have unique usernames.

---

## FR-USER-002

Username cannot be changed.

---

## FR-USER-003

Users shall edit:

Display Name

Bio

Profile Picture

---

## FR-USER-004

Users shall configure privacy settings.

---

## FR-USER-005

Allow Discover by Email

Default

ON

---

## FR-USER-006

Users shall block other users.

---

## FR-USER-007

Users shall unblock users.

---

## FR-USER-008

Users shall search using:

Username

Email (if allowed)

---

## FR-USER-009

Users shall share profile links.

Future

---

## FR-USER-010

Users shall use QR Code.

Future

---

# 3.5 Conversation Module

## FR-CONV-001

Create private conversation.

---

## FR-CONV-002

Exactly one private conversation shall exist between two users.

---

## FR-CONV-003

List user conversations.

---

## FR-CONV-004

Archive conversation.

---

## FR-CONV-005

Pin conversation.

---

## FR-CONV-006

Mute conversation.

---

## FR-CONV-007

Delete conversation locally.

---

## FR-CONV-008

Maintain unread message count.

---

## FR-CONV-009

Support pagination.

---

# 3.6 Messaging Module

## FR-MSG-001

Send text message.

Priority

Critical

---

## FR-MSG-002

Reply to message.

---

## FR-MSG-003

Forward message.

---

## FR-MSG-004

Edit message.

Maximum

15 Minutes

---

## FR-MSG-005

Delete For Me.

---

## FR-MSG-006

Delete For Everyone.

---

## FR-MSG-007

React to messages.

---

## FR-MSG-008

Remove reaction.

---

## FR-MSG-009

Support Unicode.

---

## FR-MSG-010

Support Emoji.

---

## FR-MSG-011

Support Multi-line Text.

---

## FR-MSG-012

Support Markdown.

Future

---

## FR-MSG-013

Maintain server-side ordering.

---

## FR-MSG-014

Support message status.

Sending

Sent

Delivered

Read

---

## FR-MSG-015

Support retry after network failure.

---

## FR-MSG-016

Support offline synchronization.

---

## FR-MSG-017

Support message replay after reconnect.

---

# 3.7 Media Module

## FR-MEDIA-001

Upload Image.

---

## FR-MEDIA-002

Upload Video.

---

## FR-MEDIA-003

Upload Voice Note.

---

## FR-MEDIA-004

Upload Audio.

---

## FR-MEDIA-005

Upload Documents.

---

## FR-MEDIA-006

Store files in Supabase Storage.

---

## FR-MEDIA-007

Store metadata in MongoDB.

---

## FR-MEDIA-008

Generate signed upload URLs.

---

## FR-MEDIA-009

Validate file type.

---

## FR-MEDIA-010

Validate maximum file size.

---

# 3.8 Group Module

## FR-GROUP-001

Create group.

---

## FR-GROUP-002

Edit group information.

---

## FR-GROUP-003

Invite members.

---

## FR-GROUP-004

Remove members.

---

## FR-GROUP-005

Assign roles.

---

## FR-GROUP-006

Transfer ownership.

---

## FR-GROUP-007

Generate invite links.

---

## FR-GROUP-008

Configure permissions.

---

# 3.9 Notification Module

## FR-NOTI-001

Real-time notifications.

---

## FR-NOTI-002

Unread notification count.

---

## FR-NOTI-003

Mark notification as read.

---

## FR-NOTI-004

Push Notification.

Future

---

# 3.10 Presence Module

## FR-PRESENCE-001

Online status.

---

## FR-PRESENCE-002

Offline status.

---

## FR-PRESENCE-003

Typing indicator.

---

## FR-PRESENCE-004

Typing timeout.

5 seconds

---

## FR-PRESENCE-005

Presence stored in Redis.

---

# 3.11 Search Module

## FR-SEARCH-001

Search users.

---

## FR-SEARCH-002

Search by username.

---

## FR-SEARCH-003

Search by email (privacy dependent).

---

## FR-SEARCH-004

Message search.

Future

---

## FR-SEARCH-005

Group search.

Future

---

# 3.12 Device Management

## FR-DEVICE-001

View active devices.

---

## FR-DEVICE-002

Remove device.

---

## FR-DEVICE-003

Remember login.

30 days

---

## FR-DEVICE-004

Track device metadata.

---

# 3.13 WebSocket Requirements

The system shall support:

• Secure WebSocket

• Presence

• Typing

• Messaging

• Read Receipts

• Delivery Receipts

• Notifications

• Session Recovery

• Replay

• ACK

---

# 3.14 REST API Requirements

The system shall provide:

Versioned APIs

/api/v1/

OpenAPI documentation

DTO-based requests

DTO-based responses

Validation

Standard error responses

---

# 3.15 Performance Requirements

Message latency

Target

< 300 ms

Presence updates

< 2 seconds

Authentication

< 500 ms

Media upload initiation

< 1 second

---

# 3.16 Audit Requirements

The system shall audit:

Authentication

Password changes

Group ownership changes

Permission changes

Security events

---

# 3.17 Future Functional Requirements

Voice Calling

Video Calling

Channels

Communities

Bots

AI Assistant

Message Translation

Scheduled Messages

Cloud Backup

End-to-End Encryption

Story

Status

Pinned Messages

Message Bookmarking

---

# 3.18 Functional Requirement Traceability

Each requirement shall be traceable to:

Business Requirement

Architecture

API

Database

Test Case

Implementation

Deployment

Future Maintenance

---

# 3.19 Definition of Done

A functional requirement is considered complete only when:

✓ Implemented

✓ Tested

✓ Documented

✓ Reviewed

✓ Secured

✓ Logged

✓ Monitored

✓ OpenAPI Updated

✓ Performance Verified

✓ Production Ready

---

# 3.20 Chapter Summary

This chapter defines every functional capability required by Duralap. Each requirement is uniquely identified, prioritized, and structured to ensure full traceability from business objectives through implementation, testing, deployment, and future maintenance.

Subsequent chapters describe how these functional requirements are realized through architecture, technology, database design, APIs, security, and infrastructure.

# Chapter 4 - Non-Functional Requirements

---

# Duralap Engineering Bible

## Chapter 4

**Non-Functional Requirements**

Version: 1.0

Status: Approved

---

# 4.1 Purpose

This chapter defines the quality attributes, engineering constraints, and operational characteristics that the Duralap platform must satisfy.

Unlike Functional Requirements, which describe **what the system does**, Non-Functional Requirements describe **how the system behaves** under normal and exceptional conditions.

These requirements ensure that Duralap is secure, scalable, maintainable, reliable, and production-ready.

---

# 4.2 Quality Attributes

The system shall prioritize the following quality attributes:

1. Security
2. Reliability
3. Scalability
4. Performance
5. Maintainability
6. Availability
7. Testability
8. Observability
9. Extensibility
10. Usability

---

# 4.3 Performance Requirements

## NFR-PERF-001

Authentication should complete within **500 milliseconds** under normal load.

---

## NFR-PERF-002

Text messages should be delivered to online users within **300 milliseconds**.

---

## NFR-PERF-003

Presence updates should propagate within **2 seconds**.

---

## NFR-PERF-004

Typing indicators should appear within **1 second**.

---

## NFR-PERF-005

API response time should remain below **500 milliseconds** for standard CRUD operations.

---

## NFR-PERF-006

The backend shall support pagination for all large datasets.

---

## NFR-PERF-007

Large media uploads shall not block other user operations.

---

## NFR-PERF-008

The application shall minimize unnecessary database queries.

---

# 4.4 Scalability Requirements

## NFR-SCALE-001

The backend shall support horizontal scaling.

---

## NFR-SCALE-002

The architecture shall allow migration from a Modular Monolith to Microservices with minimal changes.

---

## NFR-SCALE-003

Stateless application instances shall be preferred.

---

## NFR-SCALE-004

Redis shall be used for distributed caching and presence management.

---

## NFR-SCALE-005

Kafka shall decouple services through asynchronous event-driven communication.

---

## NFR-SCALE-006

Future deployment shall support multiple application instances behind a load balancer.

---

# 4.5 Availability Requirements

## NFR-AVAIL-001

The system should target **99.9% uptime**.

---

## NFR-AVAIL-002

Application failures should not result in permanent data loss.

---

## NFR-AVAIL-003

Graceful shutdown shall be supported.

---

## NFR-AVAIL-004

Application startup failures shall provide meaningful error logs.

---

# 4.6 Reliability Requirements

## NFR-REL-001

Messages shall never be lost after successful persistence.

---

## NFR-REL-002

Kafka consumers shall support retries.

---

## NFR-REL-003

Events shall be idempotent.

---

## NFR-REL-004

Database transactions shall guarantee consistency.

---

## NFR-REL-005

The system shall recover safely after temporary failures.

---

# 4.7 Security Requirements

## NFR-SEC-001

All communication shall use HTTPS and Secure WebSocket (WSS).

---

## NFR-SEC-002

Passwords shall be hashed using Argon2id.

---

## NFR-SEC-003

JWT Access Tokens shall be short-lived.

---

## NFR-SEC-004

Refresh Tokens shall support rotation.

---

## NFR-SEC-005

Secrets shall never be hardcoded.

---

## NFR-SEC-006

Authorization shall be validated on every protected endpoint.

---

## NFR-SEC-007

All user input shall be validated.

---

## NFR-SEC-008

Sensitive data shall never appear in logs.

---

## NFR-SEC-009

Media uploads shall validate MIME type and file size.

---

## NFR-SEC-010

Security events shall be audited.

---

# 4.8 Maintainability Requirements

## NFR-MAIN-001

The project shall follow Clean Architecture.

---

## NFR-MAIN-002

Business logic shall never reside in Controllers.

---

## NFR-MAIN-003

Repositories shall contain persistence logic only.

---

## NFR-MAIN-004

Entities shall never be exposed directly through APIs.

---

## NFR-MAIN-005

Every module shall be independently maintainable.

---

## NFR-MAIN-006

Every architectural decision shall be documented.

---

# 4.9 Testability Requirements

## NFR-TEST-001

Every business feature shall include Unit Tests.

---

## NFR-TEST-002

Critical modules shall include Integration Tests.

---

## NFR-TEST-003

Kafka components shall be integration tested.

---

## NFR-TEST-004

WebSocket functionality shall be tested.

---

## NFR-TEST-005

Minimum backend code coverage target:

**85%**

---

# 4.10 Observability Requirements

## NFR-OBS-001

Every request shall include a Correlation ID.

---

## NFR-OBS-002

Structured logging shall be used.

---

## NFR-OBS-003

Health check endpoints shall be provided.

---

## NFR-OBS-004

Metrics shall be exported to Prometheus.

---

## NFR-OBS-005

Dashboards shall be created in Grafana.

---

## NFR-OBS-006

Distributed tracing shall be supported using OpenTelemetry.

---

# 4.11 Database Requirements

## NFR-DB-001

MongoDB shall be the only source of permanent business data.

---

## NFR-DB-002

Collections shall be properly indexed.

---

## NFR-DB-003

Soft delete shall be preferred where appropriate.

---

## NFR-DB-004

Database migrations shall be backward compatible.

---

# 4.12 Redis Requirements

## NFR-REDIS-001

Redis shall only store temporary data.

---

## NFR-REDIS-002

Every Redis key shall have a TTL unless intentionally persistent.

---

## NFR-REDIS-003

Cache invalidation shall be explicitly defined.

---

## NFR-REDIS-004

Redis shall never become the source of truth.

---

# 4.13 Kafka Requirements

## NFR-KAFKA-001

Every event shall have a version.

---

## NFR-KAFKA-002

Events shall be immutable.

---

## NFR-KAFKA-003

Consumers shall be idempotent.

---

## NFR-KAFKA-004

The Outbox Pattern shall be used for reliable event publishing.

---

# 4.14 WebSocket Requirements

## NFR-WS-001

Connections shall require authentication.

---

## NFR-WS-002

Reconnect shall be supported.

---

## NFR-WS-003

Session recovery shall be supported.

---

## NFR-WS-004

Message acknowledgements shall be supported.

---

## NFR-WS-005

Events shall preserve ordering.

---

# 4.15 API Requirements

## NFR-API-001

All APIs shall be versioned.

---

## NFR-API-002

REST conventions shall be followed consistently.

---

## NFR-API-003

Standard response format shall be used across all endpoints.

---

## NFR-API-004

OpenAPI documentation shall be maintained.

---

# 4.16 Deployment Requirements

## NFR-DEP-001

Applications shall run inside Docker containers.

---

## NFR-DEP-002

Configuration shall come from environment variables.

---

## NFR-DEP-003

The application shall support Docker Compose deployment.

---

## NFR-DEP-004

Future deployments shall support Kubernetes.

---

# 4.17 Documentation Requirements

Every significant engineering decision shall be documented.

Documentation shall include:

- Architecture
- APIs
- Database
- Kafka
- Redis
- Deployment
- Security
- Testing
- Monitoring

---

# 4.18 Coding Standards

The project shall follow:

- SOLID
- DRY
- KISS
- Clean Architecture
- Repository Pattern
- DTO Pattern
- Use Case Pattern
- Event-Driven Architecture

---

# 4.19 AI Development Requirements

AI-generated code must:

- Follow all documents in `.ai/`
- Compile successfully
- Include validation
- Include logging
- Include tests
- Include documentation
- Respect architecture boundaries
- Never bypass security

---

# 4.20 Definition of Production Ready

A feature is considered production-ready only when it satisfies all of the following:

- Functional requirements implemented
- Non-functional requirements satisfied
- Architecture reviewed
- Security reviewed
- Tests passing
- Documentation updated
- Logging implemented
- Monitoring configured
- Error handling complete
- Performance verified
- OpenAPI updated
- Ready for deployment

---

# 4.21 Chapter Summary

This chapter defines the engineering quality standards that every component of Duralap must meet. These non-functional requirements ensure that the platform is not only feature-rich but also secure, scalable, reliable, maintainable, observable, and ready for real-world production deployment.

# Chapter 5 - Technology Stack

---

# Duralap Engineering Bible

## Chapter 5

**Technology Stack**

Version: 1.0

Status: Approved

---

# 5.1 Purpose

This chapter defines the official technology stack used by the Duralap platform.

Every technology included in this project has a clearly defined responsibility.

No technology should perform responsibilities outside its intended purpose.

The objective is to create a maintainable, scalable, secure, and production-ready backend architecture.

---

# 5.2 Technology Stack Overview

| Layer | Technology |
|---------|------------|
| Language | Kotlin |
| JVM | Java 21 (LTS) |
| Backend Framework | Spring Boot 3.x |
| Build Tool | Gradle Kotlin DSL |
| Database | MongoDB |
| Cache | Redis |
| Event Streaming | Apache Kafka |
| Media Storage | Supabase Storage |
| Authentication | JWT + Refresh Token + Google OAuth2 |
| Real-Time Communication | Spring WebSocket + STOMP |
| API | REST |
| Documentation | OpenAPI (Swagger) |
| Containerization | Docker |
| Local Development | Docker Compose |
| Reverse Proxy | Caddy |
| Monitoring | Prometheus |
| Dashboard | Grafana |
| Logging | Logback + SLF4J |
| Tracing | OpenTelemetry |
| Testing | JUnit 5 + MockK + Testcontainers |

---

# 5.3 Programming Language

## Kotlin

Role:

Primary backend programming language.

Reason:

- Concise syntax
- Null safety
- Coroutines
- Excellent Spring Boot support
- Strong interoperability with Java
- Modern language features

Responsibilities:

- Business Logic
- Domain Model
- APIs
- Infrastructure
- Integration

Must Never:

- Depend on Android-specific libraries.
- Include platform-specific code.

---

# 5.4 Java Runtime

## Java 21 LTS

Role:

Official JVM runtime.

Reason:

- Long-Term Support (LTS)
- Performance improvements
- Virtual Threads support (Future)
- Stability
- Enterprise adoption

---

# 5.5 Backend Framework

## Spring Boot 3

Role:

Backend application framework.

Responsibilities:

- Dependency Injection
- REST APIs
- WebSocket
- Security
- Configuration
- Validation
- Data Access
- Scheduling
- Observability

Reason:

- Mature ecosystem
- Production-ready
- Large community
- Excellent Kotlin support

---

# 5.6 Build Tool

## Gradle Kotlin DSL

Role:

Project build system.

Responsibilities:

- Dependency Management
- Build
- Testing
- Packaging
- Plugins

Reason:

- Native Kotlin support
- Incremental builds
- Flexible configuration

---

# 5.7 Database

## MongoDB

Role:

Primary Source of Truth.

Responsibilities:

- Users
- Conversations
- Messages
- Groups
- Media Metadata
- Notifications
- Audit Logs
- Outbox Events

Reason:

- Flexible document model
- High scalability
- Excellent Kotlin support
- Fast development

Must Never:

- Store cache.
- Store presence.
- Store temporary typing indicators.

---

# 5.8 Cache

## Redis

Role:

Distributed In-Memory Cache.

Responsibilities:

- Online Status
- Presence
- Typing Indicator
- Session Cache
- User Cache
- Conversation Cache
- Notification Counter
- Rate Limiting

Reason:

- Extremely fast
- TTL support
- Pub/Sub
- Distributed cache

Must Never:

- Become the source of truth.
- Store permanent business data.
- Replace MongoDB.

---

# 5.9 Event Streaming

## Apache Kafka

Role:

Distributed Event Streaming Platform.

Responsibilities:

- Domain Events
- Notification Events
- Audit Events
- Analytics Events
- Search Events
- Future Microservices Communication

Reason:

- High throughput
- Reliable event delivery
- Scalability
- Loose coupling

Must Never:

- Replace REST APIs.
- Replace MongoDB.
- Store business data permanently.

---

# 5.10 Media Storage

## Supabase Storage

Role:

Object Storage.

Responsibilities:

- Images
- Videos
- Voice Notes
- Audio
- Documents
- Attachments

MongoDB stores only metadata.

Reason:

- S3-compatible API
- Easy integration
- Cost-effective
- CDN support

Must Never:

- Store user profile data.
- Store business entities.

---

# 5.11 Authentication

Authentication consists of:

- Email + Password
- Email OTP Verification
- Google OAuth2

Future:

- Apple Login
- Passkeys
- Two-Factor Authentication

Authorization:

JWT Access Token

Refresh Token

---

# 5.12 Real-Time Communication

## Spring WebSocket

Protocol:

STOMP

Responsibilities:

- Messaging
- Typing
- Presence
- Delivery Status
- Read Receipts
- Notifications

Reason:

- Native Spring support
- Reliable messaging
- Subscription model

---

# 5.13 API Layer

Architecture:

REST

Base URL

/api/v1/

Documentation

OpenAPI

Swagger UI

Responsibilities:

- Public API
- Authentication
- Validation
- Business Operations

---

# 5.14 Containerization

## Docker

Responsibilities:

- Consistent development environment
- Local deployment
- Production packaging

Every service must run inside Docker.

---

# 5.15 Local Development

## Docker Compose

Responsibilities:

Start:

- MongoDB
- Redis
- Kafka
- Zookeeper (if required)
- Backend
- Monitoring

Single command:

docker compose up

---

# 5.16 Reverse Proxy

## Caddy

Responsibilities:

- HTTPS
- Reverse Proxy
- Automatic TLS
- Compression

Future:

Load Balancing

---

# 5.17 Monitoring

## Prometheus

Responsibilities:

- Metrics Collection

Examples:

- Request Count
- JVM Metrics
- Kafka Metrics
- Redis Metrics

---

## Grafana

Responsibilities:

- Dashboards
- Visualization
- Alerts

---

## OpenTelemetry

Responsibilities:

- Distributed Tracing
- Performance Analysis
- Request Tracking

---

# 5.18 Logging

Libraries:

SLF4J

Logback

Requirements:

Structured Logging

Correlation ID

Log Levels:

TRACE

DEBUG

INFO

WARN

ERROR

Never Log:

- Password
- JWT
- OTP
- Secrets

---

# 5.19 Testing

Frameworks:

JUnit 5

MockK

Testcontainers

Coverage Target:

Minimum

85%

Critical Modules

95%

---

# 5.20 Dependency Injection

Framework:

Spring IoC Container

Principles:

Constructor Injection

No Field Injection

Immutable Dependencies

---

# 5.21 Validation

Framework:

Jakarta Bean Validation

Responsibilities:

- Request Validation
- DTO Validation
- Input Constraints

Business validation belongs inside the application layer.

---

# 5.22 Security

Technologies:

Spring Security

JWT

Argon2id

OAuth2

Responsibilities:

- Authentication
- Authorization
- Password Hashing
- Endpoint Protection
- CSRF Strategy (where applicable)
- Rate Limiting

---

# 5.23 Version Control

Git

Repository:

GitHub

Workflow:

Git Flow

Branch Strategy:

main

develop

feature/*

release/*

hotfix/*

---

# 5.24 Future Technologies

Potential future additions:

- Kubernetes
- Helm
- Elasticsearch
- MinIO
- RabbitMQ (specific workloads)
- AI Services
- Push Notification Gateway
- WebRTC (Voice/Video Calls)
- Cloudflare CDN

Technology additions must not require major architectural redesign.

---

# 5.25 Technology Responsibility Matrix

| Technology | Primary Responsibility | Must Never Do |
|------------|------------------------|---------------|
| Kotlin | Business Logic | Platform-specific logic |
| Spring Boot | Backend Framework | Store business data |
| MongoDB | Permanent Data | Cache or presence |
| Redis | Cache & Presence | Source of truth |
| Kafka | Event Streaming | Replace REST or DB |
| Supabase Storage | Media Files | Store business entities |
| WebSocket | Real-Time Updates | Replace REST APIs |
| REST | CRUD Operations | Long-lived streaming |
| Docker | Deployment | Replace orchestration |
| Prometheus | Metrics | Log aggregation |
| Grafana | Visualization | Collect metrics |

---

# 5.26 Engineering Principles

Every technology has one primary responsibility.

The project follows the principle:

**One Responsibility Per Technology.**

Violating this principle increases system complexity and reduces maintainability.

---

# 5.27 Chapter Summary

This chapter defines the official technology stack of Duralap and establishes clear architectural boundaries for every technology.

Each component has a well-defined responsibility, ensuring that the platform remains modular, maintainable, scalable, and ready for future evolution without major redesign.


# Chapter 6 - High-Level Architecture

---

# Duralap Engineering Bible

## Chapter 6

**High-Level Architecture**

Version: 1.0

Status: Approved

---

# 6.1 Purpose

This chapter defines the overall architecture of Duralap.

It explains how the entire system is organized, how major components communicate, and how responsibilities are distributed across the backend infrastructure.

The architecture is designed to support production deployment, future scalability, and long-term maintainability.

---

# 6.2 Architecture Philosophy

Duralap follows an **Architecture-First** approach.

Before writing code, the architecture defines:

- System boundaries
- Module boundaries
- Technology responsibilities
- Communication patterns
- Data ownership
- Scalability strategy

Every implementation decision must align with this architecture.

---

# 6.3 Architectural Style

Current Architecture:

**Feature-Based Modular Monolith**

Future Architecture:

**Microservices**

The modular monolith serves as a stable foundation while keeping migration to microservices straightforward.

Each module is designed with clear boundaries and minimal coupling.

---

# 6.4 System Overview

```
                           +------------------+
                           |     Clients      |
                           +------------------+
                          Android / Web / iOS
                          Desktop / Tablet
                                   |
                                   |
                          HTTPS / WSS
                                   |
                                   ▼
                    +----------------------------+
                    |      Spring Boot API       |
                    |    (Modular Monolith)      |
                    +----------------------------+
                                   |
      ---------------------------------------------------------
      |          |           |          |         |            |
      ▼          ▼           ▼          ▼         ▼            ▼
 Identity      User    Conversation   Message   Group   Notification
      |          |           |          |         |            |
      ---------------------------------------------------------
                          Shared Infrastructure
                                   |
      ---------------------------------------------------------
      |            |               |               |
      ▼            ▼               ▼               ▼
   MongoDB      Redis          Kafka        Supabase Storage
      |
      ▼
Permanent Business Data
```

---

# 6.5 Architectural Layers

The backend is divided into logical layers.

```
Presentation Layer

↓

Application Layer

↓

Domain Layer

↓

Infrastructure Layer
```

Each layer has a single responsibility.

---

# 6.6 Client Layer

Supported clients:

- Android
- Web
- iOS
- Desktop
- Tablet

Responsibilities:

- UI
- Local Cache
- API Calls
- WebSocket Connection

Clients never contain business rules.

Business rules belong to the backend.

---

# 6.7 API Layer

Responsibilities:

- Authentication
- Request Validation
- DTO Conversion
- HTTP Response
- OpenAPI

The API layer never contains business logic.

---

# 6.8 Application Layer

Responsibilities:

- Use Cases
- Business Workflows
- Transactions
- Event Publishing
- Authorization
- Coordination

This is the heart of the application.

---

# 6.9 Domain Layer

Contains:

- Entities
- Value Objects
- Domain Services
- Domain Events

The domain layer should have no dependency on Spring Boot or infrastructure technologies.

---

# 6.10 Infrastructure Layer

Responsibilities:

- MongoDB
- Redis
- Kafka
- Supabase
- WebSocket
- Email
- External APIs

Infrastructure supports the domain but never owns business rules.

---

# 6.11 Module Architecture

The backend consists of independent feature modules.

```
Identity

User

Conversation

Message

Group

Media

Notification

Search

Infrastructure
```

Each module owns:

- Controller
- DTO
- Entity
- Repository
- Mapper
- Use Cases
- Service
- Events
- Configuration
- Exceptions

Modules communicate through:

- Facades
- Domain Events

Never through direct repository access.

---

# 6.12 Data Flow

A typical request follows this sequence:

```
Client

↓

Controller

↓

Validation

↓

Authentication

↓

Authorization

↓

Use Case

↓

Repository

↓

MongoDB

↓

Outbox Event

↓

Kafka

↓

Notification Service

↓

WebSocket

↓

Client
```

This ensures:

- Reliable persistence
- Event consistency
- Loose coupling

---

# 6.13 MongoDB Responsibility

MongoDB is the single source of truth.

Stores:

- Users
- Conversations
- Messages
- Groups
- Media Metadata
- Notifications
- Audit Logs
- Outbox Events

MongoDB never stores:

- Presence
- Typing
- Cache

---

# 6.14 Redis Responsibility

Redis stores only temporary data.

Examples:

- Presence
- Typing
- Sessions
- Cache
- Rate Limiting

Every key should have a TTL unless intentionally persistent.

Redis must never become the source of truth.

---

# 6.15 Kafka Responsibility

Kafka enables asynchronous communication.

Topics include:

- User Events
- Message Events
- Conversation Events
- Group Events
- Notification Events
- Audit Events

Future:

- Search
- Analytics
- Machine Learning

Kafka transports events but does not own business data.

---

# 6.16 Supabase Storage Responsibility

Stores:

- Images
- Videos
- Voice Notes
- Audio
- Documents

MongoDB stores only file metadata and references.

---

# 6.17 WebSocket Architecture

Real-time communication uses:

Spring WebSocket

+

STOMP

Supports:

- Messaging
- Typing
- Presence
- Read Receipts
- Delivery Receipts
- Notifications

WebSocket events occur only after successful business operations.

---

# 6.18 Authentication Flow

```
Client

↓

Login

↓

Spring Security

↓

Authentication Manager

↓

JWT Generation

↓

Access Token

+

Refresh Token
```

Protected APIs require JWT authentication.

---

# 6.19 Message Flow

```
User Sends Message

↓

Validation

↓

Authorization

↓

Persist Message

↓

MongoDB Transaction

↓

Create Outbox Event

↓

Kafka Publish

↓

Notification

↓

WebSocket Broadcast

↓

Recipient Receives Message
```

The message is never broadcast before it has been successfully persisted.

---

# 6.20 Error Handling Flow

```
Exception

↓

Global Exception Handler

↓

Standard Error Response

↓

Client
```

All errors must return a consistent response structure.

---

# 6.21 Monitoring Flow

```
Application

↓

Structured Logs

↓

Metrics

↓

Prometheus

↓

Grafana

↓

Alerts
```

Distributed tracing is handled through OpenTelemetry.

---

# 6.22 Security Architecture

Security is enforced at multiple layers.

- HTTPS/WSS
- JWT Authentication
- Authorization
- Bean Validation
- Business Validation
- Rate Limiting
- Audit Logging
- Secure Password Hashing

No single layer is solely responsible for security.

---

# 6.23 Scalability Strategy

Current:

Feature-Based Modular Monolith

Future:

```
API Gateway

↓

Identity Service

↓

User Service

↓

Conversation Service

↓

Message Service

↓

Group Service

↓

Notification Service

↓

Search Service
```

Kafka enables gradual service extraction.

---

# 6.24 Architectural Principles

The architecture follows:

- Clean Architecture
- SOLID
- DRY
- KISS
- CQRS-ready design
- Repository Pattern
- DTO Pattern
- Use Case Pattern
- Event-Driven Architecture
- Outbox Pattern
- API-First Design

---

# 6.25 Architecture Decision Records (ADR)

Significant architectural decisions should be documented using ADRs.

Examples:

- Why MongoDB was selected.
- Why Kafka was chosen.
- Why Redis stores presence.
- Why Supabase stores media.
- Why Modular Monolith precedes Microservices.

---

# 6.26 High-Level Data Ownership

| Component | Owns |
|------------|------|
| MongoDB | Permanent Business Data |
| Redis | Temporary Runtime Data |
| Kafka | Domain Events |
| Supabase Storage | Binary Media Files |
| WebSocket | Real-Time Delivery |
| Spring Boot | Business Logic |

Each component owns exactly one primary responsibility.

---

# 6.27 Engineering Principles

Every request should follow the same philosophy:

1. Validate
2. Authenticate
3. Authorize
4. Execute Business Logic
5. Persist Data
6. Publish Domain Events
7. Notify Interested Systems
8. Return Response

Skipping any step is considered an architectural violation.

---

# 6.28 Chapter Summary

This chapter establishes the overall architecture of Duralap, defines the responsibilities of each major component, explains how requests flow through the system, and provides the foundation for all implementation decisions.

Subsequent chapters describe each architectural module, data model, and infrastructure component in greater technical detail.


# Chapter 7 - Modular Monolith Architecture

---

# Duralap Engineering Bible

## Chapter 7

**Modular Monolith Architecture**

Version: 1.0

Status: Approved

---

# 7.1 Purpose

This chapter defines the internal architecture of the Duralap backend.

Although Duralap is deployed as a single Spring Boot application, it is internally divided into independent business modules.

Each module has clear responsibilities, well-defined boundaries, and communicates through controlled interfaces.

This architecture minimizes coupling while maximizing maintainability and future scalability.

---

# 7.2 Why Modular Monolith?

Duralap is intentionally designed as a Feature-Based Modular Monolith instead of Microservices.

Reasons:

- Simpler deployment
- Faster development
- Easier debugging
- Lower infrastructure cost
- Easier local development
- Single database transaction support
- Less operational complexity

The architecture is intentionally prepared for future migration to microservices.

---

# 7.3 Architecture Goals

The modular architecture must satisfy the following goals.

- High Cohesion
- Low Coupling
- Clear Ownership
- Independent Development
- Easy Testing
- Future Microservice Migration
- Business-Oriented Modules
- Stable Interfaces

---

# 7.4 Module List

The backend consists of the following business modules.

```
Identity

User

Conversation

Message

Group

Media

Notification

Search

Infrastructure
```

Future modules:

```
Channel

Community

Voice

Video

Bot

AI

Admin
```

---

# 7.5 Module Responsibilities

## Identity Module

Responsible for:

- Registration
- Login
- JWT
- Refresh Token
- Email Verification
- Password Reset
- Google OAuth2
- Device Authentication

Owns:

- User Credentials
- Authentication Tokens
- Refresh Tokens

---

## User Module

Responsible for:

- Profile
- Username
- Bio
- Avatar
- Privacy
- User Search
- Blocking
- Settings

Owns:

- User Profile
- User Privacy
- Preferences

---

## Conversation Module

Responsible for:

- Private Conversations
- Conversation List
- Conversation Metadata
- Conversation Settings
- Conversation State

Owns:

- Conversations
- Conversation Members

---

## Message Module

Responsible for:

- Sending Messages
- Editing Messages
- Reply
- Forward
- Delete
- Reactions
- Read Status
- Delivery Status

Owns:

- Messages
- Receipts
- Reactions

---

## Group Module

Responsible for:

- Groups
- Roles
- Permissions
- Ownership
- Invite Links
- Member Management

Owns:

- Groups
- Members
- Roles

---

## Media Module

Responsible for:

- Upload
- Download
- Signed URLs
- Metadata
- File Validation

Owns:

- Media Metadata

Files reside in Supabase Storage.

---

## Notification Module

Responsible for:

- Notifications
- Push Events
- Kafka Consumers
- WebSocket Broadcast

Owns:

- Notification Records

---

## Search Module

Responsible for:

- User Search
- Conversation Search

Future:

- Message Search
- Group Search

---

## Infrastructure Module

Responsible for:

- Redis
- Kafka
- MongoDB
- Email
- Security
- Configuration
- WebSocket
- Monitoring

Contains no business logic.

---

# 7.6 Module Independence

Every module owns:

- Controllers
- DTOs
- Entities
- Repositories
- Use Cases
- Services
- Events
- Exceptions
- Configuration

No module owns another module's data.

---

# 7.7 Dependency Rules

Allowed

```
Controller

↓

UseCase

↓

Repository

↓

MongoDB
```

Forbidden

```
Controller

↓

Repository
```

Forbidden

```
Controller

↓

MongoTemplate
```

Forbidden

```
Module A Repository

↓

Module B Repository
```

Forbidden

```
Entity

↓

Controller
```

---

# 7.8 Module Communication

Modules communicate only through:

- Public Facades
- Domain Events
- Shared Interfaces

Direct repository access across modules is prohibited.

Example:

```
Message Module

↓

UserFacade

↓

User Module
```

NOT

```
Message Repository

↓

User Repository
```

---

# 7.9 Internal Package Structure

Every module follows the same structure.

```
module/

    controller/

    dto/

        request/

        response/

    mapper/

    entity/

    repository/

    service/

    usecase/

    facade/

    event/

    config/

    exception/
```

Consistency across modules improves maintainability.

---

# 7.10 Layer Responsibilities

## Controller

- Receive HTTP Requests
- Validate Input
- Convert DTOs
- Return Responses

Never contains business logic.

---

## Use Case

Implements one business action.

Examples:

```
RegisterUserUseCase

SendMessageUseCase

CreateConversationUseCase

BlockUserUseCase
```

Contains application business flow.

---

## Service

Contains reusable business operations shared by multiple use cases.

Avoid creating large "God Services."

---

## Repository

Responsible only for persistence.

Never implements business rules.

---

## Entity

Represents persistent business objects.

Never exposed directly to clients.

---

## DTO

Used for API communication.

Request DTO

Response DTO

Entities never leave the application.

---

## Mapper

Converts:

DTO ↔ Entity

Entity ↔ Response

---

## Facade

Provides safe communication between modules.

Acts as the public API of a module.

---

## Events

Represents domain events.

Published after successful business operations.

---

## Config

Contains module-specific configuration.

---

## Exception

Contains module-specific exceptions.

---

# 7.11 Dependency Direction

Dependencies always point inward.

```
Controller

↓

UseCase

↓

Domain

↓

Repository Interface

↓

Infrastructure
```

Business logic never depends directly on infrastructure.

---

# 7.12 Module Boundaries

Each module owns:

- Database Collections
- Business Rules
- Validation
- Events
- API Endpoints

Other modules must respect these boundaries.

---

# 7.13 Shared Components

Allowed shared components:

- Common DTOs
- Utilities
- Constants
- Validation Helpers
- Error Codes
- Base Exceptions

Business logic should never be placed in shared packages.

---

# 7.14 Transaction Boundaries

A transaction should remain inside one business use case whenever possible.

If multiple modules are involved:

- Complete database transaction.
- Publish domain events.
- Allow asynchronous processing.

Avoid long-running transactions.

---

# 7.15 Migration to Microservices

Each module is designed to become an independent service.

Future mapping:

```
Identity Module

↓

Identity Service

------------------

User Module

↓

User Service

------------------

Conversation Module

↓

Conversation Service

------------------

Message Module

↓

Message Service

------------------

Group Module

↓

Group Service

------------------

Notification Module

↓

Notification Service
```

Because boundaries already exist, migration should require minimal business logic changes.

---

# 7.16 Advantages

This architecture provides:

- Easier Testing
- Easier Refactoring
- Lower Coupling
- Faster Development
- Better Readability
- Clear Ownership
- Future Scalability
- Reduced Technical Debt

---

# 7.17 Architecture Rules

Every new feature must:

- Belong to exactly one module.
- Follow the standard package structure.
- Respect module boundaries.
- Use DTOs.
- Use Use Cases.
- Publish domain events when appropriate.
- Avoid cross-module repository access.

Violating these rules is considered an architectural defect.

---

# 7.18 Example Module Flow

```
Client

↓

MessageController

↓

SendMessageUseCase

↓

MessageRepository

↓

MongoDB

↓

Outbox Event

↓

Kafka

↓

Notification Module

↓

WebSocket

↓

Recipient
```

This flow ensures reliable persistence, event consistency, and clean separation of responsibilities.

---

# 7.19 Architecture Review Checklist

Before merging any feature, verify:

- Correct module ownership
- No cross-module repository access
- No business logic in controllers
- DTOs used correctly
- Use Case implemented
- Validation completed
- Security enforced
- Tests added
- Documentation updated
- Domain events published where required

---

# 7.20 Chapter Summary

Duralap adopts a **Feature-Based Modular Monolith** architecture to balance simplicity with scalability.

Each module encapsulates its own business logic, persistence, APIs, and events while communicating through facades and domain events.

This architecture provides a clean, maintainable codebase today and creates a straightforward path toward future microservice extraction without major redesign.


# Chapter 8 - Module Specifications

---

# Duralap Engineering Bible

## Chapter 8

**Module Specifications**

Version: 1.0

Status: Approved

---

# 8.1 Purpose

This chapter defines every business module in Duralap.

Each module has:

- Clear Responsibility
- Data Ownership
- API Ownership
- Event Ownership
- Database Ownership
- Dependency Rules

A module is an independent business boundary.

Modules should be highly cohesive and loosely coupled.

---

# 8.2 Module List

Current Modules

```

Identity

User

Conversation

Message

Group

Media

Notification

Search

Infrastructure

```

Future Modules

```

Channel

Community

Voice

Video

Admin

AI

```

---

# 8.3 Standard Module Structure

Every module follows exactly the same package structure.

```

module-name/

controller/

dto/

request/

response/

entity/

repository/

service/

usecase/

mapper/

facade/

event/

config/

exception/

validation/

```

Every module should look identical from an architectural perspective.

---

# 8.4 Identity Module

## Purpose

Responsible for authentication and account security.

---

### Responsibilities

- User Registration
- Login
- Logout
- Refresh Token
- JWT
- Email Verification
- Password Reset
- Google OAuth2
- Device Authentication

---

### Collections

```

refresh_tokens

email_verifications

password_reset_tokens

user_devices

```

---

### APIs

```

POST /auth/register

POST /auth/login

POST /auth/google

POST /auth/verify-email

POST /auth/refresh

POST /auth/logout

POST /auth/logout-all

POST /auth/forgot-password

POST /auth/reset-password

```

---

### Published Events

```

UserRegisteredEvent

EmailVerifiedEvent

UserLoggedInEvent

UserLoggedOutEvent

PasswordChangedEvent

```

---

### Consumed Events

None

---

### Dependencies

Allowed

```

User Facade

Email Service

JWT Service

Redis

Kafka

MongoDB

```

---

# 8.5 User Module

## Purpose

Manage user profiles and privacy.

---

### Responsibilities

- Profile
- Username
- Avatar
- Bio
- Privacy
- Block User
- Search User
- Discover by Email

---

### Collections

```

users

user_profiles

user_privacy

blocked_users

```

---

### APIs

```

GET /users/me

PATCH /users/me

GET /users/{id}

GET /users/search

POST /users/block

DELETE /users/block

PATCH /users/privacy

```

---

### Published Events

```

UserProfileUpdatedEvent

UserBlockedEvent

UserUnblockedEvent

PrivacyChangedEvent

```

---

### Dependencies

Identity Facade

Redis

MongoDB

Kafka

---

# 8.6 Conversation Module

## Purpose

Manage conversations.

---

### Responsibilities

- Create Conversation
- Conversation List
- Archive
- Pin
- Mute
- Unread Count

---

### Collections

```

conversations

conversation_members

```

---

### APIs

```

POST /conversations

GET /conversations

GET /conversations/{id}

PATCH /conversations/archive

PATCH /conversations/pin

PATCH /conversations/mute

```

---

### Published Events

```

ConversationCreatedEvent

ConversationArchivedEvent

ConversationPinnedEvent

ConversationMutedEvent

```

---

### Dependencies

User Facade

Redis

MongoDB

Kafka

---

# 8.7 Message Module

## Purpose

Manage messaging.

---

### Responsibilities

- Send Message
- Edit Message
- Delete Message
- Reply
- Forward
- Reactions
- Read Receipt
- Delivery Receipt

---

### Collections

```

messages

message_receipts

message_reactions

```

---

### APIs

```

POST /messages

PATCH /messages/{id}

DELETE /messages/{id}

POST /messages/reaction

DELETE /messages/reaction

```

---

### Published Events

```

MessageSentEvent

MessageEditedEvent

MessageDeletedEvent

ReactionAddedEvent

ReactionRemovedEvent

MessageReadEvent

MessageDeliveredEvent

```

---

### Dependencies

Conversation Facade

User Facade

Redis

Kafka

MongoDB

Supabase

---

# 8.8 Group Module

## Purpose

Manage groups.

---

### Responsibilities

- Create Group
- Update Group
- Invite Members
- Remove Members
- Roles
- Permissions
- Ownership Transfer

---

### Collections

```

groups

group_members

group_settings

```

---

### APIs

```

POST /groups

PATCH /groups/{id}

DELETE /groups/{id}

POST /groups/{id}/members

DELETE /groups/{id}/members

PATCH /groups/{id}/roles

```

---

### Published Events

```

GroupCreatedEvent

MemberAddedEvent

MemberRemovedEvent

OwnershipTransferredEvent

RoleChangedEvent

```

---

# 8.9 Media Module

## Purpose

Manage media uploads.

---

### Responsibilities

- Upload
- Download
- Delete
- Metadata
- Signed URL
- Validation

---

### Collections

```

media_files

```

---

### APIs

```

POST /media/upload

GET /media/{id}

DELETE /media/{id}

```

---

### Published Events

```

MediaUploadedEvent

MediaDeletedEvent

```

---

### Dependencies

Supabase Storage

MongoDB

Kafka

---

# 8.10 Notification Module

## Purpose

Notify users.

---

### Responsibilities

- Notifications
- WebSocket
- Push Notification
- Kafka Consumers

---

### Collections

```

notifications

```

---

### APIs

```

GET /notifications

PATCH /notifications/read

```

---

### Published Events

```

NotificationCreatedEvent

NotificationReadEvent

```

---

### Consumed Events

```

MessageSentEvent

GroupCreatedEvent

UserRegisteredEvent

```

---

# 8.11 Search Module

## Purpose

Provide search functionality.

---

### Responsibilities

- User Search
- Conversation Search

Future

- Message Search
- Group Search

---

### APIs

```

GET /search/users

GET /search/conversations

```

---

### Collections

No dedicated collection.

Uses indexes from existing modules.

---

# 8.12 Infrastructure Module

## Purpose

Provide technical capabilities.

---

### Responsibilities

- Security
- Redis
- Kafka
- MongoDB
- Email
- WebSocket
- Logging
- Monitoring
- Configuration

---

### Contains

```

SecurityConfig

RedisConfig

KafkaConfig

MongoConfig

OpenApiConfig

WebSocketConfig

MailConfig

```

Business logic is prohibited.

---

# 8.13 Module Dependency Diagram

```

Identity

↓

User

↓

Conversation

↓

Message

↓

Notification

```

Media is used by Message.

Search depends on User and Conversation.

Infrastructure supports every module.

---

# 8.14 Data Ownership Matrix

| Module | Owns Collections |
|----------|------------------|
| Identity | refresh_tokens, email_verifications, password_reset_tokens, user_devices |
| User | users, user_profiles, user_privacy, blocked_users |
| Conversation | conversations, conversation_members |
| Message | messages, message_receipts, message_reactions |
| Group | groups, group_members, group_settings |
| Media | media_files |
| Notification | notifications |
| Search | None |
| Infrastructure | None |

---

# 8.15 Communication Rules

Modules communicate using:

- Facades
- Domain Events

Never:

- Repository-to-Repository
- Entity Sharing
- Cross-module Transactions

---

# 8.16 Shared Components

Allowed

- Error Codes
- Exceptions
- Utilities
- Validation Helpers
- Constants
- Base DTOs

Not Allowed

- Business Logic
- Repository Access
- Entities

---

# 8.17 Module Review Checklist

Every module must have:

✓ Controller

✓ DTO

✓ Entity

✓ Repository

✓ Use Cases

✓ Mapper

✓ Validation

✓ Exception Handling

✓ Logging

✓ Tests

✓ OpenAPI Documentation

✓ Events

✓ Configuration

---

# 8.18 Future Expansion

New modules should follow the same architecture.

Future modules include:

- Channel
- Community
- Voice
- Video
- AI
- Admin
- Analytics
- Search Service

Adding new modules must not require changes to existing module boundaries.

---

# 8.19 Engineering Principle

Every module should be capable of becoming an independent microservice in the future with minimal code changes.

The module boundary is therefore treated as a future service boundary.

---

# 8.20 Chapter Summary

This chapter defines the internal business modules of Duralap, their responsibilities, ownership, APIs, events, dependencies, and architectural boundaries. By enforcing consistent module structure and strict communication rules, Duralap maintains a clean, scalable architecture that supports future migration from a Modular Monolith to Microservices while preserving maintainability and code quality.


# Chapter 9 - Authentication & Authorization

---

# Duralap Engineering Bible

## Chapter 9

**Authentication & Authorization**

Version: 1.0

Status: Approved

---

# 9.1 Purpose

This chapter defines the authentication and authorization architecture for Duralap.

It specifies:

- User identity
- Authentication flows
- Authorization rules
- JWT lifecycle
- Refresh token management
- OAuth integration
- Device management
- Session management
- Security policies

Authentication verifies **who the user is**.

Authorization determines **what the user is allowed to do**.

---

# 9.2 Design Principles

The authentication system shall be:

- Secure
- Stateless
- Scalable
- Device-aware
- Multi-platform
- Future-proof

The design must support Android, Web, iOS, Desktop, and Tablet clients using the same backend.

---

# 9.3 Authentication Methods

Supported authentication methods:

### Email + Password

Primary authentication method.

---

### Google OAuth2

Alternative login method.

---

Future:

- Apple Sign-In
- Passkeys (WebAuthn)
- Two-Factor Authentication (2FA)

---

# 9.4 Registration Flow

User enters:

- Email
- Username
- Password

↓

Validate request

↓

Check username uniqueness

↓

Check email uniqueness

↓

Hash password using Argon2id

↓

Create inactive account

↓

Generate Email OTP

↓

Send verification email

↓

Verify OTP

↓

Activate account

↓

Generate JWT tokens

↓

Login successful

---

# 9.5 Login Flow

User enters:

- Email OR Username
- Password

↓

Find account

↓

Verify password

↓

Check email verification

↓

Check account status

↓

Generate:

- Access Token
- Refresh Token

↓

Register device session

↓

Return authentication response

---

# 9.6 Password Policy

Minimum:

8 characters

Maximum:

16 characters

Password must contain:

- Uppercase letter
- Lowercase letter
- Number
- Special character

Passwords are never stored in plain text.

---

# 9.7 Password Hashing

Algorithm:

Argon2id

Requirements:

- Salted
- Memory-hard
- Adaptive cost parameters

Passwords are never reversible.

---

# 9.8 Email Verification

Registration requires email verification.

OTP:

- Six digits
- Expires in 10 minutes
- Single use
- Maximum retry attempts configurable

Unverified accounts cannot authenticate.

---

# 9.9 Password Reset

Flow:

Forgot Password

↓

Generate reset OTP

↓

Send email

↓

Verify OTP

↓

Choose new password

↓

Invalidate all existing refresh tokens

↓

Require login again

---

# 9.10 JWT Architecture

Two-token model:

### Access Token

Purpose:

API authentication

Lifetime:

15 minutes

Contains:

- User ID
- Username
- Roles
- Token Version
- Issued At
- Expiration

---

### Refresh Token

Purpose:

Generate new Access Tokens

Lifetime:

30 days

Stored securely in the database.

Rotated after every successful refresh.

---

# 9.11 Token Lifecycle

Login

↓

Access Token + Refresh Token

↓

Access Token expires

↓

Refresh request

↓

Validate Refresh Token

↓

Issue new token pair

↓

Invalidate previous Refresh Token

---

# 9.12 Refresh Token Rotation

Every refresh operation:

- Invalidates the previous refresh token
- Creates a new refresh token
- Updates device session

This prevents replay attacks.

---

# 9.13 Logout

### Current Device

Invalidate:

- Current Refresh Token

---

### All Devices

Invalidate:

- Every Refresh Token
- Every active session

User must authenticate again on all devices.

---

# 9.14 Device Management

Each login creates a device session.

Stored information:

- Device ID
- Device Name
- Platform
- Operating System
- Browser (Web)
- IP Address
- Last Login
- Last Active
- Refresh Token ID

Users can:

- View active devices
- Remove devices
- Logout individual devices
- Logout all devices

---

# 9.15 Authorization Model

Current Role:

USER

Future Roles:

- MODERATOR
- ADMIN
- SYSTEM

Authorization follows the principle of least privilege.

---

# 9.16 Resource Ownership

Every protected resource validates ownership.

Examples:

User can edit:

Own profile

Not another user's profile.

User can delete:

Own messages

Subject to business rules.

---

# 9.17 Account Status

Possible account states:

- Pending Verification
- Active
- Suspended
- Locked
- Deleted (Soft Delete)

Inactive accounts cannot authenticate.

---

# 9.18 Failed Login Protection

Track failed login attempts.

If threshold exceeded:

- Temporarily lock account
- Require cooldown period

Future:

- CAPTCHA integration

---

# 9.19 Session Management

Session is associated with:

- User
- Device
- Refresh Token

Session expires automatically after inactivity or token expiration.

---

# 9.20 Email Discovery

Default:

Allow Discover by Email = ON

Users may disable email discoverability.

When disabled:

- Email search returns no result.
- Username search continues to work.

---

# 9.21 Username Rules

Username:

- Unique
- Case-insensitive
- Permanent
- Cannot be changed

Allowed characters:

- a-z
- 0-9
- underscore (_)
- period (.)

---

# 9.22 Security Events

Audit events:

- Registration
- Login
- Logout
- Password Change
- Password Reset
- Email Verification
- Failed Login
- Device Removal

Audit logs are immutable.

---

# 9.23 API Endpoints

Authentication APIs:

```
POST /api/v1/auth/register

POST /api/v1/auth/login

POST /api/v1/auth/google

POST /api/v1/auth/verify-email

POST /api/v1/auth/resend-verification

POST /api/v1/auth/refresh

POST /api/v1/auth/logout

POST /api/v1/auth/logout-all

POST /api/v1/auth/forgot-password

POST /api/v1/auth/reset-password
```

Device APIs:

```
GET /api/v1/devices

DELETE /api/v1/devices/{id}

DELETE /api/v1/devices
```

---

# 9.24 Security Best Practices

The authentication system shall:

- Never expose passwords
- Never log JWTs
- Never log OTPs
- Never log secrets
- Validate every request
- Rotate refresh tokens
- Hash passwords using Argon2id
- Use HTTPS/WSS only
- Enforce authorization on protected resources

---

# 9.25 Future Enhancements

Planned features:

- Two-Factor Authentication (2FA)
- Passkeys (WebAuthn)
- Apple Sign-In
- Magic Link Login
- Biometric Authentication
- Risk-Based Authentication
- Trusted Devices
- Session Approval
- Hardware Security Keys

---

# 9.26 Authentication Sequence Diagram

```
Client

↓

Register

↓

Email Verification

↓

Account Activation

↓

Login

↓

Access Token

+

Refresh Token

↓

Protected APIs

↓

Refresh

↓

New Token Pair

↓

Logout
```

---

# 9.27 Engineering Rules

Authentication code must:

- Be stateless
- Use constructor injection
- Separate authentication from authorization
- Never expose internal entities
- Use DTOs
- Publish security audit events
- Be fully unit and integration tested

---

# 9.28 Definition of Done

Authentication is complete only when:

✓ Registration implemented

✓ Email verification working

✓ Login working

✓ JWT generation implemented

✓ Refresh token rotation implemented

✓ Device management implemented

✓ Logout implemented

✓ Authorization enforced

✓ Security tests passing

✓ OpenAPI documented

✓ Production-ready

---

# 9.29 Chapter Summary

The Authentication & Authorization subsystem provides secure, stateless, and scalable identity management for Duralap. It supports email/password authentication, Google OAuth2, JWT-based authorization, refresh token rotation, device-aware sessions, and strict ownership validation while remaining extensible for future features such as Passkeys, Two-Factor Authentication, and Apple Sign-In.

# Chapter 9 - Authentication & Authorization

---

# Duralap Engineering Bible

## Chapter 9

**Authentication & Authorization**

Version: 1.0

Status: Approved

---

# 9.1 Purpose

This chapter defines the authentication and authorization architecture for Duralap.

It specifies:

- User identity
- Authentication flows
- Authorization rules
- JWT lifecycle
- Refresh token management
- OAuth integration
- Device management
- Session management
- Security policies

Authentication verifies **who the user is**.

Authorization determines **what the user is allowed to do**.

---

# 9.2 Design Principles

The authentication system shall be:

- Secure
- Stateless
- Scalable
- Device-aware
- Multi-platform
- Future-proof

The design must support Android, Web, iOS, Desktop, and Tablet clients using the same backend.

---

# 9.3 Authentication Methods

Supported authentication methods:

### Email + Password

Primary authentication method.

---

### Google OAuth2

Alternative login method.

---

Future:

- Apple Sign-In
- Passkeys (WebAuthn)
- Two-Factor Authentication (2FA)

---

# 9.4 Registration Flow

User enters:

- Email
- Username
- Password

↓

Validate request

↓

Check username uniqueness

↓

Check email uniqueness

↓

Hash password using Argon2id

↓

Create inactive account

↓

Generate Email OTP

↓

Send verification email

↓

Verify OTP

↓

Activate account

↓

Generate JWT tokens

↓

Login successful

---

# 9.5 Login Flow

User enters:

- Email OR Username
- Password

↓

Find account

↓

Verify password

↓

Check email verification

↓

Check account status

↓

Generate:

- Access Token
- Refresh Token

↓

Register device session

↓

Return authentication response

---

# 9.6 Password Policy

Minimum:

8 characters

Maximum:

16 characters

Password must contain:

- Uppercase letter
- Lowercase letter
- Number
- Special character

Passwords are never stored in plain text.

---

# 9.7 Password Hashing

Algorithm:

Argon2id

Requirements:

- Salted
- Memory-hard
- Adaptive cost parameters

Passwords are never reversible.

---

# 9.8 Email Verification

Registration requires email verification.

OTP:

- Six digits
- Expires in 10 minutes
- Single use
- Maximum retry attempts configurable

Unverified accounts cannot authenticate.

---

# 9.9 Password Reset

Flow:

Forgot Password

↓

Generate reset OTP

↓

Send email

↓

Verify OTP

↓

Choose new password

↓

Invalidate all existing refresh tokens

↓

Require login again

---

# 9.10 JWT Architecture

Two-token model:

### Access Token

Purpose:

API authentication

Lifetime:

15 minutes

Contains:

- User ID
- Username
- Roles
- Token Version
- Issued At
- Expiration

---

### Refresh Token

Purpose:

Generate new Access Tokens

Lifetime:

30 days

Stored securely in the database.

Rotated after every successful refresh.

---

# 9.11 Token Lifecycle

Login

↓

Access Token + Refresh Token

↓

Access Token expires

↓

Refresh request

↓

Validate Refresh Token

↓

Issue new token pair

↓

Invalidate previous Refresh Token

---

# 9.12 Refresh Token Rotation

Every refresh operation:

- Invalidates the previous refresh token
- Creates a new refresh token
- Updates device session

This prevents replay attacks.

---

# 9.13 Logout

### Current Device

Invalidate:

- Current Refresh Token

---

### All Devices

Invalidate:

- Every Refresh Token
- Every active session

User must authenticate again on all devices.

---

# 9.14 Device Management

Each login creates a device session.

Stored information:

- Device ID
- Device Name
- Platform
- Operating System
- Browser (Web)
- IP Address
- Last Login
- Last Active
- Refresh Token ID

Users can:

- View active devices
- Remove devices
- Logout individual devices
- Logout all devices

---

# 9.15 Authorization Model

Current Role:

USER

Future Roles:

- MODERATOR
- ADMIN
- SYSTEM

Authorization follows the principle of least privilege.

---

# 9.16 Resource Ownership

Every protected resource validates ownership.

Examples:

User can edit:

Own profile

Not another user's profile.

User can delete:

Own messages

Subject to business rules.

---

# 9.17 Account Status

Possible account states:

- Pending Verification
- Active
- Suspended
- Locked
- Deleted (Soft Delete)

Inactive accounts cannot authenticate.

---

# 9.18 Failed Login Protection

Track failed login attempts.

If threshold exceeded:

- Temporarily lock account
- Require cooldown period

Future:

- CAPTCHA integration

---

# 9.19 Session Management

Session is associated with:

- User
- Device
- Refresh Token

Session expires automatically after inactivity or token expiration.

---

# 9.20 Email Discovery

Default:

Allow Discover by Email = ON

Users may disable email discoverability.

When disabled:

- Email search returns no result.
- Username search continues to work.

---

# 9.21 Username Rules

Username:

- Unique
- Case-insensitive
- Permanent
- Cannot be changed

Allowed characters:

- a-z
- 0-9
- underscore (_)
- period (.)

---

# 9.22 Security Events

Audit events:

- Registration
- Login
- Logout
- Password Change
- Password Reset
- Email Verification
- Failed Login
- Device Removal

Audit logs are immutable.

---

# 9.23 API Endpoints

Authentication APIs:

```
POST /api/v1/auth/register

POST /api/v1/auth/login

POST /api/v1/auth/google

POST /api/v1/auth/verify-email

POST /api/v1/auth/resend-verification

POST /api/v1/auth/refresh

POST /api/v1/auth/logout

POST /api/v1/auth/logout-all

POST /api/v1/auth/forgot-password

POST /api/v1/auth/reset-password
```

Device APIs:

```
GET /api/v1/devices

DELETE /api/v1/devices/{id}

DELETE /api/v1/devices
```

---

# 9.24 Security Best Practices

The authentication system shall:

- Never expose passwords
- Never log JWTs
- Never log OTPs
- Never log secrets
- Validate every request
- Rotate refresh tokens
- Hash passwords using Argon2id
- Use HTTPS/WSS only
- Enforce authorization on protected resources

---

# 9.25 Future Enhancements

Planned features:

- Two-Factor Authentication (2FA)
- Passkeys (WebAuthn)
- Apple Sign-In
- Magic Link Login
- Biometric Authentication
- Risk-Based Authentication
- Trusted Devices
- Session Approval
- Hardware Security Keys

---

# 9.26 Authentication Sequence Diagram

```
Client

↓

Register

↓

Email Verification

↓

Account Activation

↓

Login

↓

Access Token

+

Refresh Token

↓

Protected APIs

↓

Refresh

↓

New Token Pair

↓

Logout
```

---

# 9.27 Engineering Rules

Authentication code must:

- Be stateless
- Use constructor injection
- Separate authentication from authorization
- Never expose internal entities
- Use DTOs
- Publish security audit events
- Be fully unit and integration tested

---

# 9.28 Definition of Done

Authentication is complete only when:

✓ Registration implemented

✓ Email verification working

✓ Login working

✓ JWT generation implemented

✓ Refresh token rotation implemented

✓ Device management implemented

✓ Logout implemented

✓ Authorization enforced

✓ Security tests passing

✓ OpenAPI documented

✓ Production-ready

---

# 9.29 Chapter Summary

The Authentication & Authorization subsystem provides secure, stateless, and scalable identity management for Duralap. It supports email/password authentication, Google OAuth2, JWT-based authorization, refresh token rotation, device-aware sessions, and strict ownership validation while remaining extensible for future features such as Passkeys, Two-Factor Authentication, and Apple Sign-In.


# Chapter 10 - User Management

---

# Duralap Engineering Bible

## Chapter 10

**User Management**

Version: 1.0

Status: Approved

---

# 10.1 Purpose

This chapter defines the User Management architecture of Duralap.

The User module is responsible for managing user identity beyond authentication.

It owns:

- User Profile
- Privacy Settings
- Profile Picture
- Username
- Bio
- User Search
- User Blocking
- Discoverability
- User Preferences

Authentication belongs to the Identity Module.

---

# 10.2 Module Responsibility

The User Module owns:

- User Profile
- User Settings
- Privacy
- Block List
- Public User Information

The User Module does NOT own:

- Password
- Refresh Tokens
- Login
- JWT
- Email Verification

Those belong to the Identity Module.

---

# 10.3 User Lifecycle

```
Register

↓

Verify Email

↓

Create Profile

↓

Active User

↓

Profile Updates

↓

Account Deactivation (Future)

↓

Soft Delete (Future)
```

---

# 10.4 User Profile

Every user has exactly one profile.

Fields:

- User ID
- Username
- Display Name
- Email
- Profile Picture
- Bio
- Created At
- Updated At

---

# 10.5 Username

Username is the permanent public identity.

Rules:

- Unique
- Case-insensitive
- Cannot be changed
- Used for mentions
- Used for profile links
- Used for search

Allowed characters:

- a-z
- 0-9
- underscore (_)
- period (.)

Minimum length:

3

Maximum length:

30

---

# 10.6 Display Name

Display Name is user-friendly.

Rules:

Can be changed.

Supports Unicode.

Maximum:

50 characters

Examples:

John Doe

Sazib

محمد

山田 太郎

---

# 10.7 Bio

Maximum length:

300 characters

Supports:

- Unicode
- Emoji
- Multi-line

Future:

Markdown

---

# 10.8 Profile Picture

Stored in:

Supabase Storage

MongoDB stores:

- File ID
- URL
- Metadata

Supported formats:

- JPG
- PNG
- WEBP

Future:

HEIC
AVIF

---

# 10.9 Email

Email is:

- Unique
- Verified
- Cannot be public by default

Used for:

- Login
- Password Reset
- Email Search (if enabled)

---

# 10.10 Discoverability

Users are always discoverable by:

- Username

Optional:

Email

Setting:

Allow Discover by Email

Default:

ON

If OFF:

Email search returns no results.

Username search always works.

---

# 10.11 User Search

Supported search methods:

- Username
- Email (if permitted)

Future:

- Display Name
- Phone Number
- QR Code
- Share Link

Search should support:

- Exact match
- Prefix match
- Case-insensitive search

---

# 10.12 Privacy Settings

Users control:

- Discover by Email
- Last Seen Visibility (Future)
- Online Status Visibility (Future)
- Profile Photo Visibility (Future)
- Read Receipts (Future)

Privacy defaults should favor usability while respecting user choice.

---

# 10.13 Blocking

Users may block another user.

Effects:

- Cannot send messages
- Cannot create conversations
- Hidden from search where applicable
- Presence not visible
- Typing not visible

Blocked users remain in historical conversations unless deleted.

---

# 10.14 Unblocking

Users may unblock another user.

Effects:

Messaging and discovery return to normal.

Conversation history is preserved.

---

# 10.15 User Preferences

Current preferences:

- Discover by Email

Future:

- Theme
- Language
- Notification Preferences
- Auto Download Media
- Font Size
- Accessibility Settings

---

# 10.16 Public Profile

Publicly visible:

- Username
- Display Name
- Profile Picture
- Bio

Private:

- Email
- Device Information
- Sessions
- Security Settings

---

# 10.17 User APIs

Profile APIs:

```
GET /api/v1/users/me

PATCH /api/v1/users/me

GET /api/v1/users/{id}
```

Search APIs:

```
GET /api/v1/users/search
```

Privacy APIs:

```
PATCH /api/v1/users/privacy
```

Blocking APIs:

```
POST /api/v1/users/block

DELETE /api/v1/users/block
```

---

# 10.18 Database Collections

Collections owned by User Module:

```
users

user_profiles

user_privacy

blocked_users
```

Indexes:

users

- username (unique)
- email (unique)

blocked_users

- blockerId
- blockedId

---

# 10.19 Events

Published Events:

```
UserProfileUpdatedEvent

UserBlockedEvent

UserUnblockedEvent

UserPrivacyUpdatedEvent

ProfilePictureChangedEvent
```

Consumed Events:

```
UserRegisteredEvent
```

---

# 10.20 Validation Rules

Username:

- Required
- Unique
- 3–30 characters

Display Name:

- Required
- Maximum 50 characters

Bio:

- Maximum 300 characters

Avatar:

- Valid image
- Maximum file size configurable

---

# 10.21 Security Rules

Users may only modify their own profile.

Ownership validation is mandatory.

Administrative APIs require elevated permissions (future).

---

# 10.22 Caching

Redis may cache:

- Public user profile
- Search results
- Frequently accessed user metadata

Cache invalidation occurs when:

- Profile updated
- Privacy changed
- Avatar changed

Redis never stores the source of truth.

---

# 10.23 Performance Requirements

Search response:

Target < 300 ms

Profile retrieval:

Target < 200 ms

Profile update:

Target < 500 ms

---

# 10.24 Future Enhancements

Future capabilities include:

- QR Code Profile Sharing
- Shareable Profile Links
- Username Mentions
- Verification Badges
- Custom Status
- Multiple Avatars
- Cover Photo
- Social Links
- Friend Requests (if introduced)

---

# 10.25 Engineering Rules

The User module must:

- Never manage authentication
- Never expose entities directly
- Use DTOs for all APIs
- Validate all requests
- Publish domain events
- Respect privacy settings
- Enforce ownership validation

---

# 10.26 Definition of Done

The User module is complete only when:

✓ Profile management implemented

✓ Username rules enforced

✓ Discover by Email implemented

✓ User search implemented

✓ Privacy settings implemented

✓ Blocking implemented

✓ Events published

✓ APIs documented

✓ Tests passing

✓ Production-ready

---

# 10.27 Chapter Summary

The User Management module is responsible for all profile, privacy, search, and user preference functionality within Duralap. It maintains a clear separation from authentication responsibilities, enforces ownership and privacy rules, and provides a scalable foundation for future social and collaboration features while remaining consistent with the overall modular architecture.



# Chapter 11 - Conversation Architecture

---

# Duralap Engineering Bible

## Chapter 11

**Conversation Architecture**

Version: 1.0

Status: Approved

---

# 11.1 Purpose

This chapter defines the Conversation Architecture of Duralap.

The Conversation Module is responsible for managing all communication containers between users.

It does **not** own messages.

Messages belong to the Message Module.

The Conversation Module owns only the metadata and lifecycle of conversations.

---

# 11.2 Module Responsibility

The Conversation Module is responsible for:

- Create Conversation
- Conversation Discovery
- Conversation Metadata
- Conversation Settings
- Conversation Members
- Conversation State
- Pin Conversation
- Archive Conversation
- Mute Conversation
- Unread Counter
- Last Message Reference

The module never stores message content.

---

# 11.3 Supported Conversation Types

Current

```
PRIVATE
```

Future

```
GROUP

CHANNEL

COMMUNITY
```

ConversationType Enum

```kotlin
enum class ConversationType {

    PRIVATE,

    GROUP,

    CHANNEL,

    COMMUNITY

}
```

---

# 11.4 Conversation Lifecycle

```
Create Conversation

↓

Active

↓

Pinned

↓

Muted

↓

Archived

↓

Deleted (Frontend Only)
```

Conversation deletion is currently a client-side operation.

The backend retains conversation history.

---

# 11.5 Conversation Ownership

Every conversation has:

- Conversation ID
- Type
- Members
- Settings
- Metadata
- Created At
- Updated At

Messages are stored separately.

---

# 11.6 Private Conversation Rules

Exactly two members.

Rules

- One conversation per user pair
- Cannot create duplicate conversations
- Member order is irrelevant
- Conversation is permanent

Example

```
A + B

==

B + A
```

Both reference the same conversation.

---

# 11.7 Conversation Identifier

Primary Key

MongoDB ObjectId

Example

```
687fe9e3d42ab81d3c0d4e5f
```

Client references conversations using this identifier.

---

# 11.8 Conversation Entity

```text
Conversation

--------------------

id

type

memberIds

lastMessageId

lastMessageAt

createdBy

createdAt

updatedAt

version
```

Conversation metadata only.

---

# 11.9 Conversation Member

Each member maintains independent state.

Fields

- User ID
- Joined At
- Last Read Message
- Unread Count
- Archived
- Pinned
- Muted
- Role (Future)

These values are user-specific.

---

# 11.10 Conversation Settings

Current

- Muted
- Archived
- Pinned

Future

- Wallpaper
- Notification Sound
- Auto Download
- Chat Theme
- Message Retention

---

# 11.11 Conversation Metadata

Metadata includes

- Last Message ID
- Last Message Time
- Last Sender ID
- Last Message Preview
- Unread Count
- Member Count

This enables fast conversation listing.

---

# 11.12 Conversation List

The conversation list is sorted by

```
lastMessageAt DESC
```

Newest conversations always appear first.

---

# 11.13 Conversation Search

Current

Search by

- Username

Future

- Display Name
- Conversation Name
- Message Content
- Group Name

---

# 11.14 Unread Messages

Unread count is maintained per member.

Receiving a message

↓

Unread++

Reading conversation

↓

Unread = 0

The unread counter belongs to the conversation member state.

---

# 11.15 Read Position

Each member stores

```
lastReadMessageId
```

Used for

- Read Receipts
- Unread Count
- Resume Reading

---

# 11.16 Conversation APIs

```
POST /api/v1/conversations

GET /api/v1/conversations

GET /api/v1/conversations/{id}

PATCH /api/v1/conversations/{id}/pin

PATCH /api/v1/conversations/{id}/mute

PATCH /api/v1/conversations/{id}/archive
```

Future

```
DELETE /api/v1/conversations/{id}
```

(Currently frontend-only)

---

# 11.17 Database Collections

Owned collections

```
conversations

conversation_members
```

Indexes

conversations

```
memberHash (unique)

lastMessageAt

type
```

conversation_members

```
conversationId

userId

pinned

muted

archived
```

---

# 11.18 Conversation Events

Published Events

```
ConversationCreatedEvent

ConversationPinnedEvent

ConversationMutedEvent

ConversationArchivedEvent

ConversationUpdatedEvent
```

Consumed Events

```
MessageSentEvent

MessageDeletedEvent

UserBlockedEvent
```

---

# 11.19 Redis Usage

Redis caches

- Conversation List
- Conversation Summary
- Unread Counter

TTL is configurable.

MongoDB remains the source of truth.

---

# 11.20 Kafka Integration

Conversation Module publishes

```
ConversationCreatedEvent

ConversationUpdatedEvent
```

Consumes

```
MessageSentEvent

MessageDeletedEvent
```

Kafka enables asynchronous updates without tight coupling.

---

# 11.21 WebSocket Integration

WebSocket broadcasts

- Conversation Created
- Conversation Updated
- Unread Count Updated
- Pin Status Changed
- Archive Status Changed
- Mute Status Changed

Only affected users receive updates.

---

# 11.22 Security Rules

Every request validates

- Authentication
- Membership
- Authorization
- Ownership

Users cannot access conversations they are not members of.

---

# 11.23 Validation Rules

Conversation creation validates

- Valid members
- No duplicate private conversation
- Active users only
- Not blocked (where applicable)

---

# 11.24 Performance Requirements

Conversation List

Target

< 200 ms

Conversation Creation

Target

< 500 ms

Conversation Metadata Update

Target

< 100 ms

Pagination required for all listing APIs.

---

# 11.25 Future Features

Future capabilities

- Group Conversations
- Channels
- Communities
- Conversation Folders
- Favorites
- Chat Categories
- Shared Media View
- Scheduled Conversations
- Conversation Tags

---

# 11.26 Engineering Rules

Conversation Module

Must

- Own conversation metadata
- Never store messages
- Never expose entities
- Use DTOs
- Publish events
- Validate membership
- Support pagination
- Maintain metadata consistency

Must Not

- Access Message repositories directly
- Contain authentication logic
- Manage media files

---

# 11.27 Definition of Done

The Conversation Module is complete when:

✓ Private conversations implemented

✓ Duplicate prevention enforced

✓ Conversation listing implemented

✓ Pin/Mute/Archive implemented

✓ Unread tracking implemented

✓ Events published

✓ Redis caching configured

✓ Kafka integration completed

✓ WebSocket updates working

✓ OpenAPI documented

✓ Unit tests passing

✓ Integration tests passing

✓ Production-ready

---

# 11.28 Chapter Summary

The Conversation Module is the central coordinator for all chat containers in Duralap. It manages conversation lifecycle, participant state, metadata, and synchronization while remaining independent of the Message Module. By separating conversation metadata from message storage, the architecture stays modular, scalable, and ready to support future conversation types such as Groups, Channels, and Communities.


# Chapter 12 - Messaging Architecture

---

# Duralap Engineering Bible

## Chapter 12

**Messaging Architecture**

Version: 1.0

Status: Approved

---

# 12.1 Purpose

This chapter defines the messaging architecture of Duralap.

The Message Module is responsible for the complete lifecycle of messages.

It owns:

- Message Creation
- Message Delivery
- Message Synchronization
- Message Editing
- Message Deletion
- Message Reactions
- Read Receipts
- Delivery Receipts
- Attachments
- Reply
- Forward

The Message Module does not own conversations.

Conversation metadata belongs to the Conversation Module.

---

# 12.2 Design Principles

The messaging system shall be:

- Reliable
- Ordered
- Event Driven
- Scalable
- Offline Friendly
- Multi-platform
- Fault Tolerant
- Future Ready

Every message must be persisted before being delivered.

---

# 12.3 Message Lifecycle

```
Compose

↓

Validate

↓

Authorize

↓

Persist

↓

Create Outbox Event

↓

Publish Kafka Event

↓

WebSocket Delivery

↓

Delivered

↓

Read

↓

Edited (Optional)

↓

Deleted (Optional)
```

No message is delivered before successful persistence.

---

# 12.4 Supported Message Types

Current

```
TEXT

IMAGE

VIDEO

VOICE_NOTE

AUDIO

DOCUMENT

GIF

STICKER

SHARE

CONTACT

LOCATION
```

Future

```
POLL

EVENT

LIVE_LOCATION

SYSTEM

CALL

VOICE_CALL

VIDEO_CALL
```

MessageType Enum

```kotlin
enum class MessageType {

    TEXT,

    IMAGE,

    VIDEO,

    VOICE_NOTE,

    AUDIO,

    DOCUMENT,

    GIF,

    STICKER,

    SHARE,

    CONTACT,

    LOCATION,

    POLL,

    EVENT,

    LIVE_LOCATION,

    SYSTEM

}
```

---

# 12.5 Message Entity

```
Message

-----------------------

id

conversationId

senderId

type

content

attachments

replyToMessageId

forwardedFrom

edited

deleted

createdAt

updatedAt

version
```

---

# 12.6 Message Content

Supports:

- Unicode
- Emoji
- Multi-line

Future:

- Markdown
- Rich Text

---

# 12.7 Attachments

Attachments are stored in Supabase Storage.

MongoDB stores only metadata.

Attachment metadata includes:

- Media ID
- File Name
- File Type
- MIME Type
- File Size
- Width
- Height
- Duration
- Storage Path

---

# 12.8 Reply

A reply references:

```
replyToMessageId
```

The original message remains unchanged.

Replies continue to work even if the original message is edited.

---

# 12.9 Forward

Forwarded messages preserve:

- Original Sender
- Original Timestamp (optional)
- Forwarded Flag

The new message receives a new Message ID.

---

# 12.10 Edit Message

Users may edit messages.

Maximum edit window:

15 minutes

Editing updates:

- Content
- Updated At
- Edited Flag

Edit history is not stored in Version 1.

---

# 12.11 Delete Message

Supported options:

Delete For Me

Delete For Everyone

Delete For Everyone validates:

- Sender ownership
- Allowed deletion window (configurable)

Deleted messages remain as placeholders.

Example:

"This message was deleted."

---

# 12.12 Message Reactions

Users may react using Unicode emojis.

Rules:

- One reaction per user per emoji
- Multiple different emojis allowed
- Remove reaction anytime

Reaction metadata:

- User ID
- Emoji
- Timestamp

---

# 12.13 Read Receipts

Each member tracks:

```
lastReadMessageId
```

Messages with IDs up to this value are considered read.

Read status is updated through WebSocket.

---

# 12.14 Delivery Receipts

Message states:

```
SENDING

SENT

DELIVERED

READ
```

Transitions are monotonic and never move backward.

---

# 12.15 Message Ordering

Ordering is determined by:

```
createdAt

↓

Message ID
```

Clients should preserve server-defined ordering.

---

# 12.16 Offline Synchronization

When offline:

- Queue outgoing messages locally
- Retry automatically
- Preserve message order

On reconnect:

- Synchronize unsent messages
- Replay missed events
- Refresh conversation state

---

# 12.17 Pagination

Messages use cursor-based pagination.

Example:

```
GET /messages?before=<messageId>&limit=50
```

Newest messages are loaded first.

Older messages load on demand.

---

# 12.18 Search

Current:

Not implemented.

Future:

- Text Search
- Media Search
- Link Search
- Document Search
- Message Filters

---

# 12.19 APIs

```
POST /api/v1/messages

PATCH /api/v1/messages/{id}

DELETE /api/v1/messages/{id}

POST /api/v1/messages/{id}/reply

POST /api/v1/messages/{id}/forward

POST /api/v1/messages/{id}/reactions

DELETE /api/v1/messages/{id}/reactions

GET /api/v1/messages
```

---

# 12.20 Database Collections

Owned collections

```
messages

message_reactions
```

Indexes

messages

```
conversationId + createdAt

senderId

type

createdAt
```

message_reactions

```
messageId

userId
```

---

# 12.21 Redis Usage

Redis stores:

- Temporary delivery state
- Typing indicators
- Recent message cache
- Rate limiting

Redis is never the source of truth.

---

# 12.22 Kafka Integration

Published Events

```
MessageCreatedEvent

MessageEditedEvent

MessageDeletedEvent

ReactionAddedEvent

ReactionRemovedEvent

MessageReadEvent

MessageDeliveredEvent
```

Consumed Events

```
ConversationCreatedEvent

MediaUploadedEvent
```

The Outbox Pattern guarantees reliable event publication.

---

# 12.23 WebSocket Integration

Real-time events:

- New Message
- Message Edited
- Message Deleted
- Reaction Added
- Reaction Removed
- Delivery Receipt
- Read Receipt
- Typing Indicator

All events require authenticated connections.

---

# 12.24 Security Rules

Every operation validates:

- Authentication
- Conversation Membership
- Message Ownership (where required)
- Authorization

Users cannot access messages from conversations they are not members of.

---

# 12.25 Validation Rules

Before sending:

- Conversation exists
- Sender is a member
- Message is not empty (for text)
- Attachment metadata is valid
- File type allowed
- File size within limits

---

# 12.26 Performance Requirements

Target response times:

- Send Message: < 300 ms
- Load Messages: < 200 ms
- Edit Message: < 200 ms
- Delete Message: < 200 ms

Pagination is mandatory.

---

# 12.27 Future Features

- Scheduled Messages
- Message Pinning
- Message Bookmarking
- Message Translation
- End-to-End Encryption
- Message Expiration
- AI Message Suggestions
- Voice Transcription
- Message Threads

---

# 12.28 Engineering Rules

The Message Module:

Must:

- Persist before delivery
- Use DTOs
- Publish domain events
- Support pagination
- Preserve ordering
- Validate membership
- Validate ownership
- Never expose entities

Must Not:

- Manage conversations
- Manage authentication
- Store media binaries
- Bypass Kafka for domain events

---

# 12.29 Definition of Done

The Message Module is complete when:

✓ Text messaging implemented

✓ Media messaging implemented

✓ Reply implemented

✓ Forward implemented

✓ Edit implemented

✓ Delete implemented

✓ Reactions implemented

✓ Read receipts implemented

✓ Delivery receipts implemented

✓ Redis integration completed

✓ Kafka integration completed

✓ WebSocket synchronization completed

✓ OpenAPI documented

✓ Unit tests passing

✓ Integration tests passing

✓ Production-ready

---

# 12.30 Chapter Summary

The Message Module is the core communication engine of Duralap. It manages the complete lifecycle of messages—from creation and persistence to delivery, synchronization, editing, deletion, and reactions—while maintaining reliable ordering, event-driven processing, and real-time synchronization across all supported platforms. Its architecture ensures consistency, scalability, and readiness for future capabilities such as end-to-end encryption, voice/video calling, and advanced message intelligence.



# Chapter 13 - Group Architecture

---

# Duralap Engineering Bible

## Chapter 13

**Group Architecture**

Version: 1.0

Status: Approved

---

# 13.1 Purpose

This chapter defines the Group Module architecture.

The Group Module is responsible for:

- Group Creation
- Membership
- Roles
- Permissions
- Invite Links
- Group Settings
- Moderation
- Ownership

The module is designed to support small private groups today while remaining scalable for large communities in the future.

---

# 13.2 Module Responsibility

The Group Module owns:

- Groups
- Group Members
- Group Roles
- Group Permissions
- Invite Links
- Join Requests (Future)
- Group Metadata

The Message Module owns all messages sent within groups.

---

# 13.3 Group Lifecycle

```
Create Group

↓

Active

↓

Settings Updated

↓

Members Added

↓

Members Removed

↓

Ownership Transferred

↓

Archived (Future)

↓

Deleted
```

---

# 13.4 Group Entity

```
Group

----------------------

id

name

description

avatar

ownerId

inviteLink

memberCount

settings

createdAt

updatedAt

version
```

---

# 13.5 Group Member Entity

```
GroupMember

----------------------

id

groupId

userId

role

joinedAt

lastReadMessageId

muted

nickname (Future)
```

Each member maintains independent settings.

---

# 13.6 Group Roles

Current roles:

```
OWNER

ADMIN

MEMBER
```

Future:

```
MODERATOR

BOT
```

Only one OWNER is allowed.

---

# 13.7 Role Permissions

### Owner

Can:

- Transfer ownership
- Delete group
- Change settings
- Promote/Demote admins
- Remove any member
- Create invite links

---

### Admin

Can:

- Add members
- Remove members
- Pin messages (Future)
- Update group info
- Manage invite links

Cannot:

- Delete group
- Remove owner
- Transfer ownership

---

### Member

Can:

- Send messages
- React
- Reply
- Forward
- Leave group

Cannot perform administrative actions.

---

# 13.8 Membership Rules

A user may:

- Join once
- Leave anytime
- Rejoin using invite link

Future:

- Join Requests
- Approval Workflow

---

# 13.9 Group Settings

Current:

- Group Name
- Description
- Avatar

Future:

- Join Approval
- Message Permissions
- Media Permissions
- Slow Mode
- Member Visibility
- History Visibility
- Auto Delete Messages

---

# 13.10 Invite Links

Each group owns one active invite link.

Owner/Admin may:

- Create
- Regenerate
- Disable

Future:

- Expiration Time
- Maximum Uses
- Temporary Links

---

# 13.11 Group Limits

Initial limits:

- Members: 500

Future:

- 5,000
- 20,000
- Unlimited (Communities)

Limits should be configurable.

---

# 13.12 Member Management

Supported operations:

```
Add Member

Remove Member

Leave Group

Transfer Ownership

Promote Admin

Demote Admin
```

All operations require permission validation.

---

# 13.13 Group Conversation

Each group owns exactly one conversation.

```
Group

↓

Conversation

↓

Messages
```

Conversation metadata remains in the Conversation Module.

---

# 13.14 APIs

```
POST /api/v1/groups

GET /api/v1/groups

GET /api/v1/groups/{id}

PATCH /api/v1/groups/{id}

DELETE /api/v1/groups/{id}

POST /api/v1/groups/{id}/members

DELETE /api/v1/groups/{id}/members/{userId}

PATCH /api/v1/groups/{id}/roles

POST /api/v1/groups/{id}/invite-link

POST /api/v1/groups/join
```

---

# 13.15 Database Collections

```
groups

group_members

group_invites
```

Indexes

groups

```
ownerId

createdAt
```

group_members

```
groupId

userId (unique per group)

role
```

group_invites

```
token (unique)

groupId

expiresAt
```

---

# 13.16 Events

Published Events

```
GroupCreatedEvent

GroupUpdatedEvent

GroupDeletedEvent

MemberJoinedEvent

MemberLeftEvent

MemberAddedEvent

MemberRemovedEvent

RoleChangedEvent

OwnershipTransferredEvent
```

Consumed Events

```
UserBlockedEvent

UserDeletedEvent
```

---

# 13.17 Redis Usage

Redis caches:

- Group summary
- Member count
- Frequently accessed permissions
- Active invite validation

MongoDB remains the source of truth.

---

# 13.18 Kafka Integration

Publish:

```
GroupCreatedEvent

MemberJoinedEvent

MemberRemovedEvent

RoleChangedEvent
```

Consume:

```
UserProfileUpdatedEvent

UserDeletedEvent
```

Kafka enables asynchronous notifications and analytics.

---

# 13.19 WebSocket Integration

Broadcast events:

- Member Joined
- Member Left
- Group Updated
- Role Changed
- Ownership Changed

Only connected group members receive updates.

---

# 13.20 Security Rules

Every request validates:

- Authentication
- Group membership
- Role permissions
- Resource ownership

Only authorized users may modify group settings.

---

# 13.21 Validation Rules

Validate:

- Group name required
- Group name ≤ 100 characters
- Description ≤ 500 characters
- Avatar is valid image
- Member limit not exceeded
- Invite link valid

---

# 13.22 Performance Requirements

Targets:

- Create Group: < 500 ms
- Join Group: < 300 ms
- Load Members: < 300 ms
- Update Settings: < 200 ms

Pagination required for member lists.

---

# 13.23 Future Features

- Join Requests
- Invite Approval
- Group Verification Badge
- Announcement Groups
- Topics
- Shared Albums
- Shared Calendar
- Polls
- Events
- Voice Chat
- Video Chat
- Group Bots

---

# 13.24 Engineering Rules

The Group Module:

Must:

- Own group metadata
- Validate permissions
- Publish domain events
- Use DTOs
- Never expose entities
- Maintain member consistency

Must Not:

- Store messages
- Manage authentication
- Access Message repositories directly

---

# 13.25 Definition of Done

The Group Module is complete when:

✓ Group creation implemented

✓ Member management implemented

✓ Roles implemented

✓ Permissions enforced

✓ Invite links implemented

✓ Settings implemented

✓ Events published

✓ Kafka integrated

✓ Redis caching configured

✓ WebSocket updates working

✓ OpenAPI documented

✓ Tests passing

✓ Production-ready

---

# 13.26 Chapter Summary

The Group Module provides a scalable foundation for multi-user communication in Duralap. It manages group lifecycle, membership, permissions, invite links, and moderation while remaining independent from message storage. Its architecture is designed to evolve seamlessly into large-scale communities and collaborative workspaces without requiring fundamental redesign.



# Chapter 14 - Media Architecture

---

# Duralap Engineering Bible

## Chapter 14

**Media Architecture**

Version: 1.0

Status: Approved

---

# 14.1 Purpose

This chapter defines the Media Module architecture.

The Media Module is responsible for managing all media files exchanged within Duralap.

It handles:

- Upload
- Download
- Validation
- Metadata
- Storage References
- Access Control
- Signed URLs
- Media Lifecycle

The Media Module does not permanently store binary files.

Binary files are stored in Supabase Storage.

---

# 14.2 Module Responsibility

The Media Module owns:

- Media Metadata
- Upload Process
- Download Authorization
- File Validation
- Signed URL Generation
- File Deletion
- Media Status

The module does NOT own:

- Messages
- Conversations
- Users

---

# 14.3 Supported Media Types

Current

```
IMAGE

VIDEO

VOICE_NOTE

AUDIO

DOCUMENT

GIF

STICKER
```

Future

```
LIVE_PHOTO

ANIMATION

3D_MODEL

SHORT_VIDEO

SCREEN_RECORDING
```

---

# 14.4 Supported File Formats

Images

```
JPG

JPEG

PNG

WEBP
```

Future

```
HEIC

AVIF
```

Videos

```
MP4

MOV

WEBM
```

Audio

```
MP3

AAC

M4A

OGG

WAV
```

Documents

```
PDF

DOCX

XLSX

PPTX

TXT

ZIP
```

---

# 14.5 Upload Flow

```
Client

↓

Upload Request

↓

Authentication

↓

Validation

↓

Generate Signed Upload URL

↓

Upload to Supabase Storage

↓

Store Metadata in MongoDB

↓

Publish MediaUploadedEvent

↓

Return Media ID
```

---

# 14.6 Download Flow

```
Client

↓

Request Media

↓

Authentication

↓

Authorization

↓

Generate Signed Download URL

↓

Return URL

↓

Client Downloads File
```

The backend never streams media directly.

---

# 14.7 Media Entity

```
MediaFile

------------------------

id

ownerId

conversationId

messageId

type

originalFileName

storagePath

mimeType

size

width

height

duration

checksum

status

createdAt

updatedAt

version
```

---

# 14.8 Media Status

Possible states

```
UPLOADING

UPLOADED

PROCESSING

READY

FAILED

DELETED
```

Future media processing will update these states.

---

# 14.9 Storage Strategy

Binary files

↓

Supabase Storage

Metadata

↓

MongoDB

Redis is never used for storing media.

---

# 14.10 File Validation

Every upload validates:

- MIME Type
- File Extension
- Maximum File Size
- Virus Scan (Future)
- File Integrity
- Supported Format

Invalid files are rejected.

---

# 14.11 File Size Limits

Default limits

Images

20 MB

Videos

500 MB

Voice Notes

50 MB

Audio

100 MB

Documents

100 MB

GIF

20 MB

Limits should be configurable.

---

# 14.12 Image Metadata

Store

- Width
- Height
- File Size
- MIME Type
- Orientation (Future)
- Color Profile (Future)

---

# 14.13 Video Metadata

Store

- Duration
- Width
- Height
- Codec
- Frame Rate (Future)
- Bitrate (Future)

---

# 14.14 Audio Metadata

Store

- Duration
- Codec
- Sample Rate (Future)
- Channels (Future)

---

# 14.15 Document Metadata

Store

- File Name
- Extension
- MIME Type
- Size

---

# 14.16 Signed URLs

Media access uses signed URLs.

Rules

- Temporary
- Expiring
- User Authorized

The backend never exposes permanent public URLs.

---

# 14.17 Media Ownership

Media belongs to:

- Sender
- Conversation

Only authorized conversation members may access media.

---

# 14.18 APIs

```
POST /api/v1/media/upload

GET /api/v1/media/{id}

DELETE /api/v1/media/{id}

GET /api/v1/media/{id}/download
```

Future

```
POST /api/v1/media/batch

POST /api/v1/media/thumbnail
```

---

# 14.19 Database Collection

```
media_files
```

Indexes

```
ownerId

conversationId

messageId

createdAt

type
```

---

# 14.20 Redis Usage

Redis may cache

- Signed URLs
- Upload Sessions

TTL should be short.

MongoDB remains the source of truth.

---

# 14.21 Kafka Integration

Published Events

```
MediaUploadedEvent

MediaDeletedEvent

MediaProcessingCompletedEvent

MediaProcessingFailedEvent
```

Consumed Events

```
MessageCreatedEvent

MessageDeletedEvent
```

---

# 14.22 WebSocket Integration

Broadcast

- Upload Completed
- Upload Failed
- Media Ready

Only relevant conversation members receive updates.

---

# 14.23 Security Rules

Every request validates:

- Authentication
- Conversation Membership
- Ownership
- Signed URL Expiration

Media must never be publicly accessible without authorization.

---

# 14.24 Lifecycle Management

```
Upload

↓

Validate

↓

Store

↓

Reference by Message

↓

Serve via Signed URL

↓

Delete Metadata

↓

Delete Storage Object
```

Deletion should remove both metadata and storage object.

---

# 14.25 Performance Requirements

Targets

Upload Initialization

< 200 ms

Signed URL Generation

< 100 ms

Metadata Retrieval

< 100 ms

Pagination required for media listings.

---

# 14.26 Future Features

- Automatic Image Compression
- Video Transcoding
- Thumbnail Generation
- Audio Waveform Generation
- OCR for Images
- AI Content Moderation
- Virus Scanning
- Duplicate Detection
- CDN Optimization
- Media Expiration Policies

---

# 14.27 Engineering Rules

The Media Module:

Must:

- Store metadata only
- Validate uploads
- Generate signed URLs
- Publish events
- Never expose storage internals
- Never expose permanent URLs

Must Not:

- Store binary files in MongoDB
- Bypass authorization
- Access Message repositories directly

---

# 14.28 Definition of Done

The Media Module is complete when:

✓ Upload implemented

✓ Download implemented

✓ Validation implemented

✓ Metadata stored

✓ Signed URLs implemented

✓ Authorization enforced

✓ Kafka integrated

✓ Redis caching configured

✓ WebSocket notifications working

✓ OpenAPI documented

✓ Tests passing

✓ Production-ready

---

# 14.29 Chapter Summary

The Media Module provides secure and scalable media management for Duralap by separating binary file storage from application metadata. It uses Supabase Storage for objects, MongoDB for metadata, Redis for short-lived caching, Kafka for asynchronous events, and WebSocket for real-time updates. This architecture supports reliable file sharing today while providing a foundation for advanced media processing and optimization in future releases.


# Chapter 15 - Notification Architecture

---

# Duralap Engineering Bible

## Chapter 15

**Notification Architecture**

Version: 1.0

Status: Approved

---

# 15.1 Purpose

This chapter defines the Notification Module architecture.

The Notification Module is responsible for delivering system-generated notifications to users.

It supports:

- In-App Notifications
- Real-Time Notifications
- Push Notifications (Future)
- Email Notifications (Future)
- Notification Preferences
- Notification History

The Notification Module never owns business logic.

It reacts to events published by other modules.

---

# 15.2 Module Responsibility

The Notification Module owns:

- Notification Records
- Notification Delivery
- Notification Status
- Notification Preferences
- WebSocket Delivery
- Push Delivery (Future)

It does NOT own:

- Messages
- Conversations
- Users
- Authentication

---

# 15.3 Notification Sources

Notifications originate from domain events.

Examples:

Identity Module

- UserRegisteredEvent

User Module

- UserBlockedEvent

Conversation Module

- ConversationCreatedEvent

Message Module

- MessageCreatedEvent
- MessageReactionAddedEvent
- MessageReadEvent

Group Module

- MemberAddedEvent
- MemberRemovedEvent
- RoleChangedEvent

Media Module

- MediaUploadedEvent

---

# 15.4 Notification Types

Current

```
NEW_MESSAGE

MESSAGE_REACTION

MESSAGE_READ

GROUP_INVITE

GROUP_MEMBER_ADDED

SYSTEM
```

Future

```
FRIEND_REQUEST

ACCOUNT_SECURITY

CALL

VOICE_CALL

VIDEO_CALL

CHANNEL_POST

COMMUNITY_UPDATE

AI_NOTIFICATION
```

---

# 15.5 Notification Lifecycle

```
Domain Event

↓

Kafka Event

↓

Notification Created

↓

Store in MongoDB

↓

WebSocket Delivery

↓

Client Receives

↓

Marked Read

↓

Archived (Future)
```

---

# 15.6 Notification Entity

```
Notification

------------------------

id

userId

type

title

body

data

status

createdAt

readAt

expiresAt

version
```

---

# 15.7 Notification Status

Possible states

```
CREATED

DELIVERED

READ

EXPIRED

DELETED
```

State transitions are one-way.

---

# 15.8 Notification Payload

Payload includes:

- Notification ID
- Type
- Title
- Body
- Related Resource ID
- Deep Link
- Timestamp

Example

```
{
    "type": "NEW_MESSAGE",
    "conversationId": "...",
    "messageId": "...",
    "senderId": "..."
}
```

---

# 15.9 Notification Preferences

Users may configure:

Current

- Enable Notifications

Future

- Message Notifications
- Group Notifications
- Mention Notifications
- Email Notifications
- Push Notifications
- Sound
- Vibration

---

# 15.10 Notification Delivery

Priority

```
Kafka Event

↓

Notification Service

↓

MongoDB

↓

WebSocket

↓

Client
```

Future

```
↓

Firebase Cloud Messaging

↓

Apple Push Notification Service

↓

Email

↓

SMS
```

---

# 15.11 In-App Notifications

Displayed inside Duralap.

Features

- Notification Center
- Read Status
- Pagination
- Deep Linking

---

# 15.12 Push Notifications (Future)

Provider

```
Firebase Cloud Messaging (FCM)
```

Future

```
Apple Push Notification Service (APNs)
```

Supports:

- Background Delivery
- Badge Count
- Notification Actions

---

# 15.13 Notification APIs

```
GET /api/v1/notifications

PATCH /api/v1/notifications/{id}/read

PATCH /api/v1/notifications/read-all

DELETE /api/v1/notifications/{id}
```

Future

```
PATCH /api/v1/notifications/preferences
```

---

# 15.14 Database Collections

```
notifications
```

Indexes

```
userId

status

createdAt

expiresAt
```

TTL Index

```
expiresAt
```

Expired notifications are removed automatically.

---

# 15.15 Redis Usage

Redis stores:

- Unread Notification Count
- Recent Notification Cache

TTL is configurable.

MongoDB remains the source of truth.

---

# 15.16 Kafka Integration

Consumes

```
UserRegisteredEvent

MessageCreatedEvent

MessageReactionAddedEvent

MemberAddedEvent

RoleChangedEvent

MediaUploadedEvent
```

Publishes

```
NotificationCreatedEvent

NotificationDeliveredEvent

NotificationReadEvent
```

Kafka enables asynchronous notification processing.

---

# 15.17 WebSocket Integration

Broadcasts

- New Notification
- Notification Updated
- Notification Read
- Badge Count Updated

Only the target user receives these events.

---

# 15.18 Security Rules

Every notification request validates:

- Authentication
- Ownership

Users can only access their own notifications.

---

# 15.19 Validation Rules

Validate:

- Valid notification type
- Valid target user
- Payload size
- Resource existence
- Expiration timestamp

---

# 15.20 Notification Retention

Current

Notifications remain until:

- User deletes them
- TTL expires

Future

Retention policy configurable per notification type.

---

# 15.21 Performance Requirements

Targets

Create Notification

< 100 ms

Retrieve Notifications

< 200 ms

Unread Count

< 50 ms

WebSocket Delivery

< 100 ms

Pagination required.

---

# 15.22 Future Features

- Push Notifications
- Email Notifications
- SMS Notifications
- Scheduled Notifications
- Notification Categories
- Smart Notification Bundling
- Snooze Notifications
- AI Priority Ranking
- Quiet Hours

---

# 15.23 Engineering Rules

The Notification Module:

Must:

- Consume Kafka events
- Store notifications
- Publish WebSocket events
- Use DTOs
- Validate ownership
- Respect user preferences

Must Not:

- Contain business logic
- Access Message repositories directly
- Generate business events

---

# 15.24 Definition of Done

The Notification Module is complete when:

✓ Notification storage implemented

✓ WebSocket delivery implemented

✓ Kafka consumers implemented

✓ Read status implemented

✓ Notification APIs implemented

✓ Redis unread counter implemented

✓ OpenAPI documented

✓ Unit tests passing

✓ Integration tests passing

✓ Production-ready

---

# 15.25 Chapter Summary

The Notification Module provides a centralized, event-driven notification system for Duralap. It consumes domain events from Kafka, persists notifications in MongoDB, delivers them in real time via WebSocket, and prepares the platform for future push, email, and SMS notification channels. By separating notification delivery from business logic, the architecture remains scalable, maintainable, and extensible.


# Chapter 16 - Search Architecture

---

# Duralap Engineering Bible

## Chapter 16

**Search Architecture**

Version: 1.0

Status: Approved

---

# 16.1 Purpose

This chapter defines the Search Module architecture.

The Search Module provides fast, secure, and privacy-aware search capabilities across Duralap.

Current responsibilities:

- User Search
- Conversation Search

Future responsibilities:

- Message Search
- Group Search
- Channel Search
- Community Search
- Media Search

The Search Module does not own business data.

It queries data owned by other modules while respecting their privacy and authorization rules.

---

# 16.2 Design Principles

Search must be:

- Fast
- Secure
- Privacy-aware
- Scalable
- Extensible
- Case-insensitive
- Unicode-compatible

Search should never expose unauthorized resources.

---

# 16.3 Module Responsibility

The Search Module owns:

- Search APIs
- Search DTOs
- Search Queries
- Search Ranking
- Search Filters

The module does NOT own:

- Users
- Conversations
- Messages
- Groups

---

# 16.4 Search Categories

Current

```
USER

CONVERSATION
```

Future

```
MESSAGE

GROUP

CHANNEL

COMMUNITY

MEDIA
```

---

# 16.5 User Search

Supported methods:

- Username
- Email (if discoverable)

Future

- Display Name
- QR Code
- Share Link

Rules

- Username is always searchable.
- Email search respects user privacy settings.
- Results are case-insensitive.
- Supports Unicode.

---

# 16.6 Conversation Search

Current

Search by:

- Participant Username

Future

- Group Name
- Channel Name
- Community Name
- Conversation Tags

Users only receive conversations they are members of.

---

# 16.7 Search Flow

```
Client

↓

Search Request

↓

Authentication

↓

Authorization

↓

Search Service

↓

MongoDB

↓

Rank Results

↓

Return DTOs
```

---

# 16.8 Search APIs

```
GET /api/v1/search/users

GET /api/v1/search/conversations
```

Future

```
GET /api/v1/search/messages

GET /api/v1/search/groups

GET /api/v1/search/media
```

---

# 16.9 Search Request

Example

```
GET /api/v1/search/users?q=sazib
```

Supported parameters

```
q

limit

cursor
```

---

# 16.10 Search Response

Example

```json
{
  "items": [
    {
      "id": "...",
      "username": "sazib",
      "displayName": "Sazib Hossain",
      "avatar": "..."
    }
  ],
  "nextCursor": "..."
}
```

Responses always use DTOs.

---

# 16.11 Search Ranking

Current ranking

Priority

1. Exact Match

2. Prefix Match

3. Partial Match

Example

Searching

```
john
```

Results

```
john

john123

john_doe

myjohn
```

---

# 16.12 Pagination

All search endpoints use cursor-based pagination.

Example

```
GET /search/users?q=sazib&cursor=...&limit=20
```

No offset pagination.

---

# 16.13 Authorization

Every search validates:

- Authentication
- Privacy Settings
- Membership (for conversations)

Search results are filtered before returning to the client.

---

# 16.14 Privacy Rules

Username

Always searchable.

Email

Searchable only when

```
Allow Discover by Email = ON
```

Blocked users should not appear in search results where business rules require.

---

# 16.15 Database Strategy

Current implementation:

MongoDB indexes

Future implementation:

Dedicated Search Engine

Examples

- OpenSearch
- Elasticsearch

The API should remain unchanged during migration.

---

# 16.16 MongoDB Indexes

Users

```
username

email

displayName (future)
```

Conversations

```
lastMessageAt

memberIds
```

Indexes should support the most common search patterns.

---

# 16.17 Redis Usage

Redis may cache:

- Popular search results
- Recently searched users
- Frequently accessed conversation summaries

TTL should be configurable.

Redis never becomes the source of truth.

---

# 16.18 Kafka Integration

Current

No Kafka dependency.

Future

Consume:

```
UserProfileUpdatedEvent

ConversationUpdatedEvent

MessageCreatedEvent
```

Purpose

Update external search indexes asynchronously.

---

# 16.19 WebSocket Integration

Current

Not required.

Future

Real-time search updates for:

- Username changes (if ever allowed)
- Group name updates
- Presence-aware search

---

# 16.20 Performance Requirements

Targets

User Search

< 150 ms

Conversation Search

< 200 ms

Search should support:

- Pagination
- Result limits
- Stable ordering

---

# 16.21 Search Filters

Current

None

Future

Users

- Online Only
- Verified
- Recently Active

Messages

- Media Only
- Links Only
- Documents Only
- Date Range
- Sender

Groups

- Public
- Private

---

# 16.22 Search Analytics

Future

Track anonymously:

- Popular searches
- Failed searches
- Search latency

Personally identifiable search history should not be stored without explicit user consent.

---

# 16.23 Future Features

- Full-text Search
- Fuzzy Search
- Typo Tolerance
- Search Suggestions
- Search History
- Saved Searches
- AI-powered Search
- Semantic Search
- Voice Search
- OCR Search for Images

---

# 16.24 Engineering Rules

The Search Module:

Must:

- Respect authorization
- Respect privacy settings
- Use DTOs
- Support pagination
- Be case-insensitive
- Support Unicode

Must Not:

- Own business data
- Modify business data
- Expose hidden users
- Bypass module boundaries

---

# 16.25 Definition of Done

The Search Module is complete when:

✓ User search implemented

✓ Conversation search implemented

✓ Email privacy enforced

✓ Cursor pagination implemented

✓ MongoDB indexes optimized

✓ DTOs implemented

✓ OpenAPI documented

✓ Tests passing

✓ Production-ready

---

# 16.26 Chapter Summary

The Search Module provides secure, privacy-aware, and scalable discovery capabilities across Duralap. It currently supports user and conversation search using optimized MongoDB indexes while maintaining stable APIs that can later transition to OpenSearch or Elasticsearch. By separating search logic from business modules, the architecture remains modular, maintainable, and future-ready.


# Chapter 17 - MongoDB Design

---

# Duralap Engineering Bible

## Chapter 17

**MongoDB Design**

Version: 1.0

Status: Approved

---

# 17.1 Purpose

This chapter defines the MongoDB architecture used by Duralap.

MongoDB is the primary persistent datastore and the single source of truth for all business data.

It stores:

- Users
- Conversations
- Messages
- Groups
- Notifications
- Media Metadata
- Authentication Data
- Audit Logs
- Outbox Events

Redis is used only for temporary runtime data.

---

# 17.2 Design Principles

The database design follows these principles:

- Document-oriented
- Aggregate-based
- High Read Performance
- High Write Performance
- Horizontal Scalability
- Minimal Transactions
- Event-Driven Consistency

---

# 17.3 Source of Truth

MongoDB owns:

✓ Permanent Business Data

Redis owns:

✓ Temporary Runtime State

Kafka owns:

✓ Event Transport

Supabase owns:

✓ Binary Files

---

# 17.4 Collections

Current collections

```
users

refresh_tokens

email_verifications

password_reset_tokens

user_devices

blocked_users

conversations

conversation_members

messages

message_reactions

groups

group_members

group_invites

media_files

notifications

outbox_events

audit_logs
```

Future

```
channels

communities

calls

voice_calls

video_calls

search_indexes
```

---

# 17.5 Collection Ownership

| Collection | Module |
|------------|---------|
| users | User |
| refresh_tokens | Identity |
| email_verifications | Identity |
| password_reset_tokens | Identity |
| user_devices | Identity |
| blocked_users | User |
| conversations | Conversation |
| conversation_members | Conversation |
| messages | Message |
| message_reactions | Message |
| groups | Group |
| group_members | Group |
| group_invites | Group |
| media_files | Media |
| notifications | Notification |
| outbox_events | Infrastructure |
| audit_logs | Infrastructure |

---

# 17.6 Document Design

Documents should represent business aggregates.

Avoid excessive normalization.

Prefer embedding for:

- Small
- Stable
- Frequently accessed

Prefer references for:

- Large
- Frequently changing
- Many-to-many relationships

---

# 17.7 ObjectId Strategy

Every document uses:

```
_id : ObjectId
```

ObjectId is used as the primary identifier.

No UUIDs are used in Version 1.

---

# 17.8 Users Collection

Example

```json
{
  "_id": ObjectId,
  "username": "sazib",
  "displayName": "Sazib Hossain",
  "email": "user@example.com",
  "avatar": "...",
  "bio": "...",

  "privacy": {
    "discoverByEmail": true
  },

  "preferences": {
    "language": "en",
    "theme": "system"
  },

  "createdAt": "...",
  "updatedAt": "..."
}
```

---

# 17.9 Conversations Collection

Stores only metadata.

Contains:

- Type
- Last Message
- Last Activity
- Creator

Does NOT contain:

- Messages
- Member-specific state

---

# 17.10 Conversation Members

Separate collection.

Contains:

- User ID
- Conversation ID
- Unread Count
- Last Read Message
- Pinned
- Archived
- Muted

Reason:

Member state changes frequently.

---

# 17.11 Messages Collection

Contains:

- Conversation ID
- Sender ID
- Type
- Content
- Attachments
- Reply Reference
- Deleted Flag
- Edited Flag
- Created At

Messages never embed conversations.

---

# 17.12 Groups Collection

Stores:

- Metadata
- Owner
- Settings
- Member Count

Members stored separately.

---

# 17.13 Media Collection

Stores only metadata.

Never stores binary content.

---

# 17.14 Notifications Collection

Stores:

- Target User
- Payload
- Status
- Read Time
- Expiration

TTL Index removes expired notifications.

---

# 17.15 Audit Logs

Immutable.

Contains:

- Actor
- Action
- Resource
- Timestamp
- Metadata

Never modified.

---

# 17.16 Outbox Events

Supports the Outbox Pattern.

Fields

```
eventType

aggregateId

payload

status

createdAt

publishedAt
```

Used to guarantee reliable Kafka publication.

---

# 17.17 Relationships

Reference-based relationships.

Examples

```
Message

↓

Conversation

↓

User
```

No cascading deletes.

---

# 17.18 Index Strategy

Users

```
username (unique)

email (unique)
```

Messages

```
conversationId + createdAt

senderId
```

Conversation Members

```
conversationId

userId

conversationId + userId (unique)
```

Groups

```
ownerId
```

Notifications

```
userId

createdAt

expiresAt (TTL)
```

Indexes should match query patterns.

---

# 17.19 Transactions

Use MongoDB transactions only when necessary.

Examples

Good:

- Registration
- Message + Outbox Event

Avoid long-running transactions.

---

# 17.20 Soft Delete

Current

Messages

```
deleted = true
```

Future

Users

Groups

Conversations

Soft deletion preserves history.

---

# 17.21 Schema Versioning

Every major document includes:

```
version
```

Supports future migrations.

---

# 17.22 Optimistic Locking

Frequently updated documents include:

```
version
```

Increment on every update.

Prevents lost updates.

---

# 17.23 Validation

MongoDB schema validation should enforce:

- Required fields
- Enum values
- Data types

Application validation remains mandatory.

---

# 17.24 Performance Guidelines

Avoid:

- Large documents (>16 MB)
- Unbounded arrays
- N+1 queries
- Collection scans

Prefer:

- Covered indexes
- Cursor pagination
- Bulk writes
- Projection queries

---

# 17.25 Naming Conventions

Collections

```
snake_case
```

Examples

```
conversation_members

message_reactions

user_devices
```

Fields

```
camelCase
```

Examples

```
createdAt

updatedAt

conversationId
```

---

# 17.26 Backup Strategy

Daily backups.

Point-in-time recovery.

Regular restore testing.

Backups must be encrypted.

---

# 17.27 Monitoring

Monitor:

- Query latency
- Index usage
- Slow queries
- Storage growth
- Cache hit ratio
- Transaction failures

---

# 17.28 Future Sharding

Initial deployment:

Single Replica Set

Future

Shard by:

Messages

```
conversationId
```

Notifications

```
userId
```

Shard keys must minimize hotspots.

---

# 17.29 Engineering Rules

Every collection must:

- Have clear ownership
- Have required indexes
- Use ObjectId
- Include timestamps
- Avoid duplicate data
- Follow module boundaries

Business logic must never depend on MongoDB-specific features.

---

# 17.30 Definition of Done

MongoDB architecture is complete when:

✓ Collections defined

✓ Ownership documented

✓ Indexes optimized

✓ Transactions implemented

✓ Outbox Pattern implemented

✓ Validation configured

✓ Backup strategy documented

✓ Monitoring configured

✓ Migration strategy documented

✓ Production-ready

---

# 17.31 Chapter Summary

MongoDB serves as the authoritative datastore for Duralap, providing scalable document storage, optimized indexing, reliable transactions where required, and clear ownership boundaries for every business module. The schema design emphasizes performance, maintainability, and future scalability while remaining aligned with the platform's Modular Monolith architecture and planned evolution toward microservices.




# Chapter 18 - Redis Design

---

# Duralap Engineering Bible

## Chapter 18

**Redis Design**

Version: 1.0

Status: Approved

---

# 18.1 Purpose

This chapter defines the Redis architecture used by Duralap.

Redis provides high-speed, in-memory data storage for temporary runtime state.

Redis is NOT the primary database.

MongoDB remains the source of truth.

Redis stores:

- Cache
- Presence
- Typing Status
- Sessions
- Rate Limiting
- WebSocket Runtime Data
- Counters
- Temporary Tokens

---

# 18.2 Design Principles

Redis usage follows these principles:

- Cache Only
- Ephemeral Data
- Fast Read
- Fast Write
- Automatic Expiration
- No Business Ownership
- Easily Rebuildable

If Redis is lost, the application must continue operating.

---

# 18.3 Responsibilities

Redis is responsible for:

- Frequently accessed data
- Runtime state
- Temporary counters
- Presence tracking
- Online users
- WebSocket sessions
- Rate limiting

Redis never stores permanent business data.

---

# 18.4 Source of Truth

Permanent Data

↓

MongoDB

Temporary Data

↓

Redis

Events

↓

Kafka

Media

↓

Supabase Storage

---

# 18.5 Redis Data Categories

Current

```
Cache

Presence

Typing

Unread Counters

Notification Counters

Signed URLs

Rate Limits

WebSocket Sessions
```

Future

```
Call Sessions

Video Rooms

Temporary Invitations

AI Context Cache

Search Suggestions
```

---

# 18.6 Key Naming Convention

Format

```
module:resource:identifier
```

Examples

```
user:presence:687fa...

conversation:summary:687fa...

conversation:typing:687fa...

notification:count:user123

media:url:media123

rate_limit:login:192.168.1.10

ws:session:user123
```

Use lowercase with colon separators.

---

# 18.7 TTL Policy

Every temporary key should have a TTL unless it represents an active session.

Recommended defaults:

| Data | TTL |
|------|-----|
| Typing Indicator | 15 sec |
| Presence | 60 sec |
| Signed URL Cache | 5 min |
| Notification Counter Cache | 5 min |
| Conversation Summary Cache | 10 min |
| Search Cache | 10 min |
| Rate Limit | Configurable |
| Upload Session | 15 min |

---

# 18.8 Presence Tracking

Store:

```
user:presence:{userId}
```

Value

```
ONLINE

OFFLINE

LAST_SEEN
```

Presence automatically expires.

Heartbeat refreshes TTL.

---

# 18.9 Typing Indicator

```
conversation:typing:{conversationId}
```

Stores

```
userId

startedAt
```

TTL

15 seconds

Typing indicators never persist in MongoDB.

---

# 18.10 Conversation Cache

Cache

- Conversation Summary
- Last Message Preview
- Member Count

Cache is invalidated when:

- New Message
- Member Added
- Member Removed
- Conversation Updated

---

# 18.11 Notification Cache

Cache

Unread notification count

```
notification:count:{userId}
```

Updated on:

- Notification Created
- Notification Read
- Notification Deleted

---

# 18.12 Signed URL Cache

Cache generated signed URLs.

Benefits

- Reduce storage API calls
- Improve response time

TTL should always be shorter than the actual URL expiration.

---

# 18.13 Rate Limiting

Examples

```
Login

OTP

Registration

Message Send

Media Upload

Search
```

Redis increments counters atomically.

Expiration resets the limit automatically.

---

# 18.14 WebSocket Sessions

Store

```
ws:session:{userId}
```

Contains

- Session ID
- Connected Server
- Connected At

Used for targeted event delivery.

---

# 18.15 Distributed Locking

Future support

Redis distributed locks

Used for

- Scheduled Jobs
- Singleton Tasks
- Leader Election

Implementation

```
SET NX PX
```

---

# 18.16 Cache Invalidation

Cache invalidation occurs after successful MongoDB commit.

Strategies

- Delete Cache
- Refresh Cache
- Lazy Reload

Never invalidate before persistence succeeds.

---

# 18.17 Redis Data Structures

Strings

- Presence
- Counters

Hashes

- Conversation Summary
- User Session

Sets

- Online Users
- Typing Users

Sorted Sets

- Leaderboards (Future)
- Recent Searches (Future)

Streams (Future)

- Analytics

---

# 18.18 High Availability

Initial

Single Redis Instance

Production

Redis Sentinel

Future

Redis Cluster

Application must support failover.

---

# 18.19 Monitoring

Monitor

- Memory Usage
- Hit Ratio
- Evictions
- Expired Keys
- Command Latency
- Connection Count

Alerts should be configured for abnormal conditions.

---

# 18.20 Performance Targets

Cache Read

< 5 ms

Cache Write

< 5 ms

Presence Update

< 10 ms

Rate Limit Check

< 5 ms

---

# 18.21 Redis Integration

Modules using Redis

Identity

- Rate Limiting
- OTP Cache

User

- Presence

Conversation

- Conversation Summary

Message

- Typing Indicator

Notification

- Notification Count

Media

- Signed URL Cache

Search

- Popular Search Cache

---

# 18.22 Failure Strategy

If Redis becomes unavailable:

- Continue using MongoDB
- Disable cache temporarily
- Skip presence updates
- Skip typing indicators
- Continue message delivery

Redis failure must not stop the application.

---

# 18.23 Security

Redis must:

- Require authentication
- Run on a private network
- Disable dangerous commands
- Encrypt traffic (TLS in production)

Sensitive data should never be stored unencrypted.

---

# 18.24 Engineering Rules

Redis:

Must:

- Store temporary data only
- Use TTL where appropriate
- Be fully rebuildable
- Support cache invalidation
- Never become the source of truth

Must Not:

- Store business entities
- Replace MongoDB
- Store media files
- Store permanent conversations

---

# 18.25 Definition of Done

Redis architecture is complete when:

✓ Key naming standardized

✓ TTL policies defined

✓ Presence tracking implemented

✓ Typing indicators implemented

✓ Notification counters cached

✓ Conversation cache implemented

✓ Rate limiting implemented

✓ WebSocket sessions tracked

✓ Monitoring configured

✓ Production-ready

---

# 18.26 Chapter Summary

Redis provides the high-performance runtime layer for Duralap by managing temporary state, caching frequently accessed data, tracking presence and typing indicators, enforcing rate limits, and supporting WebSocket routing. By ensuring that all cached data is disposable and MongoDB remains the authoritative datastore, the platform achieves low latency without sacrificing consistency or reliability.



# Chapter 19 - Kafka Design

---

# Duralap Engineering Bible

## Chapter 19

**Kafka Design**

Version: 1.0

Status: Approved

---

# 19.1 Purpose

This chapter defines the Apache Kafka architecture used by Duralap.

Kafka is the event backbone of the platform.

Its responsibilities include:

- Event Publishing
- Event Delivery
- Asynchronous Communication
- Module Decoupling
- Reliable Processing
- Event Replay
- Scalability

Kafka is NOT a database.

MongoDB remains the source of truth.

---

# 19.2 Design Principles

Kafka usage follows these principles:

- Event Driven
- Asynchronous
- Reliable
- Decoupled
- Idempotent
- Replayable
- Observable

Every business event should be immutable.

---

# 19.3 Responsibilities

Kafka is responsible for:

- Transporting domain events
- Connecting modules
- Reliable event delivery
- Event replay
- Decoupling business modules

Kafka does NOT own business data.

---

# 19.4 Event Flow

```
HTTP Request

↓

Business Logic

↓

MongoDB Transaction

↓

Outbox Event

↓

Outbox Publisher

↓

Kafka Topic

↓

Consumer

↓

Business Action
```

No Kafka event is published before the database transaction succeeds.

---

# 19.5 Event Categories

Current

```
Identity Events

User Events

Conversation Events

Message Events

Group Events

Media Events

Notification Events
```

Future

```
Call Events

Channel Events

Community Events

Analytics Events

Search Index Events
```

---

# 19.6 Topic Naming Convention

Format

```
duralap.<module>.<event>
```

Examples

```
duralap.identity.user_registered

duralap.user.profile_updated

duralap.conversation.created

duralap.message.created

duralap.message.deleted

duralap.group.member_added

duralap.media.uploaded

duralap.notification.created
```

Use lowercase with dot separators.

---

# 19.7 Event Structure

Every event contains:

```
eventId

eventType

aggregateId

aggregateType

version

occurredAt

producer

payload
```

Example

```json
{
  "eventId": "...",
  "eventType": "MessageCreated",
  "aggregateId": "...",
  "aggregateType": "Message",
  "version": 1,
  "occurredAt": "...",
  "producer": "message-module",
  "payload": { }
}
```

---

# 19.8 Event Versioning

Each event includes

```
version
```

Versioning enables backward compatibility.

Consumers must tolerate older versions.

---

# 19.9 Producers

Modules publishing events

Identity

- UserRegisteredEvent
- PasswordChangedEvent

User

- UserProfileUpdatedEvent
- UserBlockedEvent

Conversation

- ConversationCreatedEvent

Message

- MessageCreatedEvent
- MessageEditedEvent
- MessageDeletedEvent

Group

- MemberJoinedEvent
- MemberRemovedEvent

Media

- MediaUploadedEvent

Notification

- NotificationCreatedEvent

---

# 19.10 Consumers

Examples

Notification Module

Consumes

- MessageCreatedEvent
- MemberJoinedEvent

Conversation Module

Consumes

- MessageCreatedEvent

Search Module (Future)

Consumes

- UserProfileUpdatedEvent
- GroupUpdatedEvent

Analytics Module (Future)

Consumes all business events.

---

# 19.11 Outbox Pattern

Every event is first written into

```
outbox_events
```

Flow

```
Business Transaction

↓

Outbox Insert

↓

Commit

↓

Kafka Publisher

↓

Topic

↓

Mark Published
```

This guarantees reliable event publishing.

---

# 19.12 Message Ordering

Ordering is guaranteed within a partition.

Partition key recommendations:

Messages

```
conversationId
```

Notifications

```
userId
```

Groups

```
groupId
```

This preserves logical ordering.

---

# 19.13 Delivery Guarantees

Producer

```
At Least Once
```

Consumer

Must be idempotent.

Duplicate events must not cause inconsistent state.

---

# 19.14 Retry Strategy

On failure

```
Retry

↓

Exponential Backoff

↓

Dead Letter Queue
```

Retries should be configurable.

---

# 19.15 Dead Letter Queue

Every critical topic has a DLQ.

Example

```
duralap.message.created.dlq
```

Failed events are inspected and replayed when appropriate.

---

# 19.16 Event Replay

Kafka allows replay by resetting consumer offsets.

Use cases:

- Search Index Rebuild
- Analytics Reprocessing
- Recovery after outages
- New Consumer Bootstrapping

---

# 19.17 Serialization

Current

```
JSON
```

Future

```
Apache Avro

Protocol Buffers
```

Schema evolution should be supported.

---

# 19.18 Consumer Groups

Each module owns its own consumer group.

Examples

```
notification-service

conversation-service

search-service

analytics-service
```

This enables independent scaling.

---

# 19.19 Monitoring

Monitor

- Consumer Lag
- Topic Throughput
- Publish Rate
- Failed Events
- DLQ Size
- Processing Time

Alerts should be configured.

---

# 19.20 Security

Kafka should use

- TLS
- SASL Authentication
- ACLs

Only authorized producers and consumers may access topics.

---

# 19.21 Performance Targets

Publish Event

< 50 ms

Consumer Processing

< 200 ms

Outbox Publisher

< 100 ms

Consumer Lag

< 5 seconds

---

# 19.22 Event Ownership

Each event has exactly one producer.

Many consumers are allowed.

Never allow multiple modules to publish the same event type.

---

# 19.23 Engineering Rules

Kafka:

Must:

- Publish immutable events
- Use Outbox Pattern
- Support retries
- Support DLQs
- Version events
- Monitor consumer lag

Must Not:

- Replace MongoDB
- Contain business logic
- Share mutable event payloads
- Perform synchronous request-response

---

# 19.24 Definition of Done

Kafka architecture is complete when:

✓ Topics defined

✓ Naming standardized

✓ Producers implemented

✓ Consumers implemented

✓ Outbox Pattern implemented

✓ Retry strategy implemented

✓ DLQs configured

✓ Monitoring configured

✓ Security configured

✓ Production-ready

---

# 19.25 Chapter Summary

Apache Kafka provides the event-driven backbone of Duralap by enabling reliable, asynchronous communication between modules. Through immutable events, the Outbox Pattern, idempotent consumers, and replay capabilities, Kafka decouples business logic while ensuring scalability, resilience, and future readiness for distributed systems and microservices.


# Chapter 20 - WebSocket Protocol

---

# Duralap Engineering Bible

## Chapter 20

**WebSocket Protocol**

Version: 1.0

Status: Approved

---

# 20.1 Purpose

This chapter defines the WebSocket protocol used by Duralap.

WebSocket provides bidirectional, low-latency communication between clients and the backend.

Responsibilities:

- Real-time Messaging
- Presence Updates
- Typing Indicators
- Read Receipts
- Delivery Receipts
- Notification Delivery
- Conversation Updates
- Group Updates

HTTP remains responsible for request-response APIs.

---

# 20.2 Design Principles

The protocol must be:

- Reliable
- Authenticated
- Ordered
- Secure
- Event Driven
- Reconnect Friendly
- Stateless

The server must never trust client-generated events without validation.

---

# 20.3 Technology Stack

Backend

- Spring WebSocket
- STOMP

Future

- SockJS (optional fallback)

Clients

- Android
- Web
- iOS (Future)
- Desktop (Future)

---

# 20.4 Connection Flow

```
Client

↓

HTTP Login

↓

JWT Access Token

↓

WebSocket Connect

↓

JWT Validation

↓

Connection Established

↓

Subscribe

↓

Receive Events
```

---

# 20.5 Authentication

Every WebSocket connection requires a valid JWT.

Example

```
Authorization:

Bearer <access-token>
```

Unauthenticated connections are rejected.

---

# 20.6 Connection Lifecycle

```
CONNECT

↓

AUTHENTICATED

↓

SUBSCRIBED

↓

ACTIVE

↓

DISCONNECTED

↓

RECONNECTED
```

---

# 20.7 STOMP Destinations

Application

```
/app/*
```

User Queue

```
/user/queue/*
```

Topics

```
/topic/*
```

---

# 20.8 Client Send Destinations

Examples

```
/app/chat.send

/app/chat.typing

/app/chat.read

/app/chat.delivered

/app/presence

/app/reaction.add

/app/reaction.remove
```

---

# 20.9 Server Push Destinations

Examples

```
/user/queue/messages

/user/queue/notifications

/user/queue/presence

/user/queue/read-receipts

/user/queue/delivery-receipts

/user/queue/errors
```

Group broadcasts

```
/topic/group/{groupId}
```

---

# 20.10 Event Types

Current

```
MESSAGE_CREATED

MESSAGE_EDITED

MESSAGE_DELETED

REACTION_ADDED

REACTION_REMOVED

READ_RECEIPT

DELIVERY_RECEIPT

TYPING_STARTED

TYPING_STOPPED

USER_ONLINE

USER_OFFLINE

NOTIFICATION_CREATED
```

Future

```
CALL_STARTED

VOICE_CALL

VIDEO_CALL

LIVE_LOCATION

POLL_UPDATED
```

---

# 20.11 Event Envelope

Every event follows a common structure.

```json
{
  "eventId": "...",
  "eventType": "MESSAGE_CREATED",
  "timestamp": "...",
  "conversationId": "...",
  "payload": {}
}
```

---

# 20.12 Subscription Rules

Users may subscribe only to resources they are authorized to access.

Examples

Allowed

```
Own notification queue

Own message queue

Joined group topics
```

Rejected

```
Other user's queue

Groups not joined

Unauthorized conversations
```

---

# 20.13 Presence Protocol

Presence states

```
ONLINE

OFFLINE

AWAY (Future)

DO_NOT_DISTURB (Future)
```

Presence updates are published through Redis and WebSocket.

---

# 20.14 Typing Protocol

Flow

```
Typing Started

↓

Broadcast

↓

TTL Expired

↓

Typing Stopped
```

Typing indicators are not persisted.

---

# 20.15 Read Receipts

Flow

```
Client Opens Conversation

↓

Send Read Event

↓

Update Database

↓

Publish WebSocket Event

↓

Notify Other Members
```

---

# 20.16 Delivery Receipts

Message states

```
SENDING

↓

SENT

↓

DELIVERED

↓

READ
```

States are monotonic.

---

# 20.17 Reconnection

On reconnect

```
Reconnect

↓

Authenticate

↓

Restore Subscriptions

↓

Replay Missed Events

↓

Resume Normal Operation
```

The client should retry with exponential backoff.

---

# 20.18 Heartbeat

STOMP heartbeats detect broken connections.

Recommended interval

```
30 seconds
```

Heartbeat timeout triggers disconnect handling.

---

# 20.19 Error Handling

Server returns structured errors.

Example

```json
{
  "code": "UNAUTHORIZED",
  "message": "Invalid JWT"
}
```

Common errors

```
UNAUTHORIZED

FORBIDDEN

INVALID_PAYLOAD

RESOURCE_NOT_FOUND

RATE_LIMITED
```

---

# 20.20 Ordering

Ordering is guaranteed per conversation.

Ordering source

```
Server Timestamp

↓

Message Sequence

↓

Message ID
```

Clients must display messages in server-defined order.

---

# 20.21 Rate Limiting

Limit events such as:

- Typing
- Presence
- Reactions
- Read Receipts

Abusive clients should be throttled.

---

# 20.22 Security Rules

Validate:

- JWT
- Membership
- Ownership
- Payload
- Event Type

Never trust client-provided identifiers without verification.

---

# 20.23 Redis Integration

Redis supports:

- Presence
- Typing
- WebSocket Session Mapping
- Pub/Sub

Redis coordinates multiple backend instances.

---

# 20.24 Kafka Integration

Business events are published to Kafka.

WebSocket broadcasts are generated after successful event processing.

Kafka remains the event backbone.

---

# 20.25 Performance Targets

Connection

< 500 ms

Event Delivery

< 100 ms

Typing Update

< 50 ms

Presence Update

< 100 ms

---

# 20.26 Future Features

- End-to-End Encrypted Events
- Voice Calls
- Video Calls
- Live Location Streaming
- Message Streaming
- Presence Sync Across Devices
- Collaborative Features

---

# 20.27 Engineering Rules

The WebSocket layer:

Must:

- Authenticate every connection
- Validate subscriptions
- Use DTOs
- Publish structured events
- Support reconnect
- Support heartbeat
- Maintain ordering

Must Not:

- Contain business logic
- Access MongoDB directly
- Bypass authorization
- Replace HTTP APIs

---

# 20.28 Definition of Done

The WebSocket protocol is complete when:

✓ JWT authentication implemented

✓ STOMP destinations configured

✓ Message events implemented

✓ Presence implemented

✓ Typing indicators implemented

✓ Read receipts implemented

✓ Delivery receipts implemented

✓ Notification delivery implemented

✓ Redis Pub/Sub integrated

✓ Kafka integration completed

✓ Reconnection supported

✓ OpenAPI/WebSocket documentation completed

✓ Tests passing

✓ Production-ready

---

# 20.29 Chapter Summary

The WebSocket Protocol provides Duralap's real-time communication layer. It enables secure, authenticated, bidirectional communication for messaging, presence, typing indicators, receipts, notifications, and future collaboration features. By combining Spring WebSocket, STOMP, Redis, and Kafka, the protocol delivers low-latency communication while remaining scalable, resilient, and ready for multi-instance deployments.



# Chapter 21 - REST API Standards

---

# Duralap Engineering Bible

## Chapter 21

**REST API Standards**

Version: 1.0

Status: Approved

---

# 21.1 Purpose

This chapter defines the REST API standards for Duralap.

Every HTTP endpoint must follow these standards.

Goals

- Consistency
- Simplicity
- Predictability
- Security
- Versioning
- Scalability

All modules must follow the same API conventions.

---

# 21.2 API Design Principles

Every API should be:

- RESTful
- Stateless
- Versioned
- Secure
- Idempotent where applicable
- Self-descriptive
- DTO-based

Business entities must never be exposed directly.

---

# 21.3 Base URL

Current

```
/api/v1
```

Examples

```
/api/v1/auth

/api/v1/users

/api/v1/messages

/api/v1/groups
```

Future API versions

```
/api/v2
```

Old versions remain supported until officially deprecated.

---

# 21.4 HTTP Methods

GET

Retrieve resources

POST

Create resources

PUT

Replace entire resource

PATCH

Partial update

DELETE

Delete resource

Never misuse HTTP verbs.

---

# 21.5 Resource Naming

Use nouns.

Good

```
/users

/messages

/groups

/conversations

/notifications
```

Avoid verbs.

Bad

```
/getUsers

/createMessage

/deleteConversation
```

Use lowercase.

Use plural nouns.

---

# 21.6 URI Standards

Good

```
GET /users/{id}

GET /groups/{id}/members

POST /messages

PATCH /messages/{id}

DELETE /messages/{id}
```

Avoid deeply nested URLs.

Maximum nesting depth

```
3
```

---

# 21.7 Request Headers

Common headers

```
Authorization

Content-Type

Accept

X-Request-ID

X-Correlation-ID
```

Example

```
Authorization:

Bearer <access-token>
```

---

# 21.8 Content Types

Primary

```
application/json
```

Uploads

```
multipart/form-data
```

Downloads

```
application/octet-stream
```

---

# 21.9 Request DTOs

Every request uses DTOs.

Never expose entities.

Example

```
SendMessageRequest

CreateGroupRequest

UpdateProfileRequest
```

---

# 21.10 Response DTOs

Every response uses DTOs.

Example

```
UserResponse

MessageResponse

ConversationResponse

GroupResponse
```

Entities never leave the service layer.

---

# 21.11 Success Responses

GET

```
200 OK
```

POST

```
201 Created
```

DELETE

```
204 No Content
```

PATCH

```
200 OK
```

---

# 21.12 Error Responses

Use standard HTTP status codes.

```
400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

409 Conflict

422 Unprocessable Entity

429 Too Many Requests

500 Internal Server Error
```

---

# 21.13 Error Format

All errors follow the same structure.

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "code": "USER_NOT_FOUND",
  "message": "User does not exist.",
  "path": "/api/v1/users/123",
  "requestId": "..."
}
```

Never expose stack traces.

---

# 21.14 Pagination

Cursor-based pagination only.

Example

```
GET /messages?cursor=...&limit=50
```

Never use offset pagination.

Default

20

Maximum

100

---

# 21.15 Sorting

Supported parameter

```
sort
```

Example

```
sort=createdAt,desc
```

---

# 21.16 Filtering

Supported parameter

```
filter
```

Example

```
status=ACTIVE
```

---

# 21.17 Searching

Parameter

```
q
```

Example

```
GET /search/users?q=sazib
```

---

# 21.18 Idempotency

POST endpoints supporting retries should accept:

```
Idempotency-Key
```

Examples

- Payments (Future)
- Registration
- Media Upload Initialization

Repeated requests with the same key should not create duplicates.

---

# 21.19 Validation

Validate:

- Required fields
- String length
- Enum values
- File size
- Email format
- Username format

Validation occurs before business logic.

---

# 21.20 Authentication

Protected endpoints require JWT.

Header

```
Authorization:

Bearer <token>
```

Unauthenticated requests return

```
401 Unauthorized
```

---

# 21.21 Authorization

Every protected endpoint validates:

- Ownership
- Membership
- Role
- Permissions

Authentication alone is never sufficient.

---

# 21.22 API Versioning

Current

```
v1
```

Future

```
v2

v3
```

Breaking changes require a new API version.

---

# 21.23 Rate Limiting

Rate limiting applies to:

- Login
- Registration
- OTP
- Password Reset
- Search
- Media Upload

Returns

```
429 Too Many Requests
```

---

# 21.24 Logging

Log

- Request ID
- User ID
- Response Time
- Status Code
- Endpoint

Sensitive data must never be logged.

---

# 21.25 OpenAPI

Every endpoint must include:

- Summary
- Description
- Request Example
- Response Example
- Error Responses
- Security Requirements

Swagger documentation is mandatory.

---

# 21.26 Deprecation

Deprecated APIs include:

```
Deprecation: true

Sunset: <date>
```

Clients should receive migration guidance.

---

# 21.27 Security

APIs must:

- Validate JWT
- Validate input
- Escape output
- Prevent mass assignment
- Prevent IDOR
- Use HTTPS
- Enforce CORS policy

---

# 21.28 Performance Targets

GET

< 200 ms

POST

< 300 ms

PATCH

< 250 ms

DELETE

< 200 ms

Pagination required for collection endpoints.

---

# 21.29 Engineering Rules

Every API:

Must

- Use DTOs
- Return proper HTTP status codes
- Validate requests
- Authenticate users
- Authorize actions
- Return standardized errors
- Support OpenAPI

Must Not

- Return entities
- Leak stack traces
- Leak sensitive data
- Mix business logic with controllers

---

# 21.30 Definition of Done

REST API standards are complete when:

✓ Resource naming standardized

✓ DTOs implemented

✓ Error format standardized

✓ Pagination standardized

✓ Validation enforced

✓ JWT authentication integrated

✓ Authorization implemented

✓ OpenAPI documented

✓ Logging configured

✓ Rate limiting implemented

✓ Production-ready

---

# 21.31 Chapter Summary

The REST API Standards establish a consistent contract between Duralap clients and backend services. By enforcing uniform resource naming, DTO-based communication, standardized errors, JWT security, cursor pagination, and OpenAPI documentation, the platform delivers APIs that are predictable, secure, maintainable, and ready for long-term evolution.


# Chapter 22 - Error Handling

---

# Duralap Engineering Bible

## Chapter 22

**Error Handling**

Version: 1.0

Status: Approved

---

# 22.1 Purpose

This chapter defines the error handling architecture used throughout Duralap.

Goals

- Consistent error responses
- Predictable behavior
- Secure error reporting
- Easy debugging
- API consistency
- Observability

Every module follows the same error handling strategy.

---

# 22.2 Design Principles

Errors must be:

- Standardized
- Predictable
- Secure
- Actionable
- Logged
- Traceable

Never expose internal implementation details.

---

# 22.3 Error Categories

Business Errors

Examples

- Username already exists
- Conversation not found
- User blocked
- Invalid group invitation

Validation Errors

Examples

- Invalid email
- Missing field
- Invalid enum value

Authentication Errors

Examples

- Missing JWT
- Expired JWT
- Invalid Refresh Token

Authorization Errors

Examples

- Not conversation member
- Not group admin
- Access denied

Infrastructure Errors

Examples

- MongoDB unavailable
- Redis unavailable
- Kafka unavailable
- Storage unavailable

System Errors

Examples

- Null pointer
- Unexpected exception
- Serialization failure

---

# 22.4 Error Response Format

Every API returns the same structure.

```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "code": "USER_NOT_FOUND",
  "message": "The requested user does not exist.",
  "path": "/api/v1/users/123",
  "requestId": "abc-123"
}
```

---

# 22.5 HTTP Status Codes

```
200 OK

201 Created

204 No Content

400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

409 Conflict

410 Gone

422 Unprocessable Entity

429 Too Many Requests

500 Internal Server Error

503 Service Unavailable
```

---

# 22.6 Error Codes

Every business error has a unique code.

Examples

```
USER_NOT_FOUND

USERNAME_ALREADY_EXISTS

EMAIL_ALREADY_EXISTS

INVALID_PASSWORD

TOKEN_EXPIRED

INVALID_TOKEN

ACCESS_DENIED

CONVERSATION_NOT_FOUND

MESSAGE_NOT_FOUND

GROUP_NOT_FOUND

MEDIA_NOT_FOUND

RATE_LIMIT_EXCEEDED

INTERNAL_ERROR
```

Error codes are immutable.

---

# 22.7 Exception Hierarchy

```
ApplicationException

├── ValidationException

├── AuthenticationException

├── AuthorizationException

├── BusinessException

├── ResourceNotFoundException

├── ConflictException

├── InfrastructureException

└── InternalServerException
```

Every custom exception extends the base ApplicationException.

---

# 22.8 Global Exception Handler

Use Spring Boot's

```
@RestControllerAdvice
```

Responsibilities

- Catch exceptions
- Map HTTP status codes
- Build ErrorResponse DTO
- Log appropriately

Controllers never handle exceptions directly.

---

# 22.9 Validation Errors

Bean Validation failures return

```
400 Bad Request
```

Example

```json
{
  "code": "VALIDATION_FAILED",
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email"
    }
  ]
}
```

---

# 22.10 Authentication Errors

Examples

```
JWT expired

JWT invalid

Refresh token expired

Missing Authorization header
```

Always return

```
401 Unauthorized
```

Never reveal why authentication failed in detail.

---

# 22.11 Authorization Errors

Examples

```
Not conversation member

Not group owner

Not group admin

Blocked user
```

Return

```
403 Forbidden
```

---

# 22.12 Resource Not Found

Examples

```
User

Conversation

Message

Group

Notification
```

Return

```
404 Not Found
```

---

# 22.13 Conflict Errors

Examples

```
Duplicate username

Duplicate email

Already group member

Already reacted
```

Return

```
409 Conflict
```

---

# 22.14 Rate Limiting

Exceeded limits return

```
429 Too Many Requests
```

Include

```
Retry-After
```

header where appropriate.

---

# 22.15 Infrastructure Errors

Examples

```
Mongo timeout

Redis unavailable

Kafka unavailable

Supabase unavailable
```

Return

```
503 Service Unavailable
```

Application should degrade gracefully whenever possible.

---

# 22.16 Logging

Every error log includes

- Request ID
- Correlation ID
- User ID (if authenticated)
- Endpoint
- HTTP Method
- Error Code
- Stack Trace (server only)

Sensitive information must never be logged.

---

# 22.17 Client Messages

Client messages must

- Be human-readable
- Never expose stack traces
- Never expose SQL/Mongo queries
- Never expose internal class names

---

# 22.18 Retry Policy

Retry only for transient failures.

Examples

Retry

- Kafka timeout
- Redis timeout
- Temporary network issue

Do NOT retry

- Validation errors
- Authentication failures
- Authorization failures

---

# 22.19 WebSocket Errors

Error events follow

```json
{
  "eventType": "ERROR",
  "code": "ACCESS_DENIED",
  "message": "You are not authorized."
}
```

WebSocket errors never disconnect clients unless required.

---

# 22.20 Kafka Error Handling

Consumers must

- Retry configurable times
- Use exponential backoff
- Send failed events to DLQ
- Log correlation ID

Consumers must be idempotent.

---

# 22.21 Redis Error Handling

If Redis fails

- Skip cache
- Continue with MongoDB
- Log warning
- Do not fail user requests unnecessarily

---

# 22.22 Security

Errors must never reveal

- Passwords
- JWT contents
- Internal IPs
- Stack traces
- Database queries
- Server paths

---

# 22.23 Monitoring

Track

- Error Rate
- 4xx Rate
- 5xx Rate
- Top Error Codes
- Slow Requests
- Failed Kafka Consumers

Alerts should be configured.

---

# 22.24 Engineering Rules

Error handling must

- Be centralized
- Use DTOs
- Use immutable error codes
- Return proper HTTP status codes
- Log server-side details only
- Include request identifiers

Must Not

- Return stack traces
- Leak implementation details
- Handle exceptions inside controllers
- Ignore unexpected exceptions

---

# 22.25 Definition of Done

The Error Handling architecture is complete when:

✓ Global exception handler implemented

✓ Standard ErrorResponse DTO implemented

✓ Custom exception hierarchy defined

✓ Validation errors standardized

✓ JWT errors standardized

✓ Authorization errors standardized

✓ Infrastructure failures handled

✓ WebSocket errors standardized

✓ Kafka retry and DLQ configured

✓ Logging configured

✓ Monitoring dashboards created

✓ Production-ready

---

# 22.26 Chapter Summary

Duralap uses a centralized error handling architecture that ensures all failures are reported consistently, securely, and predictably. Through standardized error responses, a global exception handler, immutable error codes, structured logging, and graceful degradation for infrastructure failures, the platform provides a reliable experience for both users and developers while maintaining enterprise-grade observability and security.



# Chapter 23 - Security Blueprint

---

# Duralap Engineering Bible

## Chapter 23

**Security Blueprint**

Version: 1.0

Status: Approved

---

# 23.1 Purpose

This chapter defines the enterprise security architecture for Duralap.

Security applies to every module.

Goals

- Confidentiality
- Integrity
- Availability
- Accountability
- Privacy
- Resilience

Every component follows Security by Design.

---

# 23.2 Security Principles

Duralap follows:

- Defense in Depth
- Zero Trust
- Least Privilege
- Fail Secure
- Secure by Default
- Principle of Separation
- Immutable Audit Logs

Never trust:

- Client
- Device
- Network
- External services

Everything is validated.

---

# 23.3 Security Layers

```
Internet

↓

Load Balancer

↓

HTTPS

↓

Spring Security

↓

JWT Authentication

↓

Authorization

↓

Validation

↓

Business Rules

↓

MongoDB

↓

Audit Logs
```

Security exists at every layer.

---

# 23.4 Authentication

Authentication uses

JWT Access Token

JWT Refresh Token

Password Hashing

Device Tracking

Email Verification

Future

Passkeys

OAuth2

MFA

---

# 23.5 Password Security

Algorithm

```
Argon2id
```

Requirements

Minimum

12 characters

Require

Uppercase

Lowercase

Number

Special Character

Passwords are never stored or logged.

---

# 23.6 JWT Security

Access Token

Short-lived

```
15 minutes
```

Refresh Token

```
30 days
```

Every JWT contains

```
userId

deviceId

roles

issuedAt

expiresAt

jwtId
```

JWTs are signed using strong keys.

---

# 23.7 Refresh Token Security

Stored in MongoDB

Hashed before storage

Bound to device

Revocable

Rotated after every refresh

Old refresh tokens become invalid.

---

# 23.8 Device Management

Each login creates

```
UserDevice
```

Stores

- Device ID
- Device Name
- Platform
- Last Login
- IP
- User Agent

Users may revoke any device.

---

# 23.9 Authorization

Authorization is role-based.

Current

```
USER

ADMIN
```

Future

```
MODERATOR

SUPPORT

SYSTEM
```

Authorization also validates

Ownership

Membership

Resource Access

---

# 23.10 Resource Ownership

Examples

Only sender may edit message.

Only owner may delete group.

Only conversation member may read messages.

Every request validates ownership.

---

# 23.11 Input Validation

Validate

- DTOs
- File Type
- File Size
- Username
- Email
- Password
- Enums
- ObjectId

Reject invalid requests immediately.

---

# 23.12 Output Security

Never expose

- Password Hash
- Refresh Token
- Internal IDs
- Secrets
- Stack Traces

Only DTOs leave the service layer.

---

# 23.13 Transport Security

Production requires

HTTPS only

TLS 1.3 preferred

HSTS enabled

Secure Cookies (if applicable)

Plain HTTP is never allowed.

---

# 23.14 CORS Policy

Allow only trusted origins.

Methods

GET

POST

PATCH

DELETE

Headers

Authorization

Content-Type

Reject wildcard origins in production.

---

# 23.15 CSRF

Current

JWT Authentication

CSRF disabled.

Future

If cookie authentication is introduced,

Enable CSRF protection.

---

# 23.16 Rate Limiting

Protect

Login

Registration

Password Reset

OTP

Search

Media Upload

Message Sending

Redis stores counters.

---

# 23.17 File Upload Security

Validate

- MIME Type
- File Extension
- Maximum Size

Reject executable files.

Store metadata only.

Media stored in Supabase Storage.

Future

Virus scanning.

---

# 23.18 WebSocket Security

Validate

JWT

Membership

Subscriptions

Message Payload

Prevent unauthorized subscriptions.

---

# 23.19 Kafka Security

Use

TLS

SASL

ACLs

Only approved producers and consumers may access topics.

---

# 23.20 Redis Security

Redis must

Require authentication

Run on private network

Disable dangerous commands

Enable TLS

Never expose Redis publicly.

---

# 23.21 MongoDB Security

Enable

Authentication

TLS

Role-based access

Encrypted backups

Disable public access.

---

# 23.22 Secrets Management

Secrets never appear in source code.

Examples

JWT Secret

Mongo URI

Redis Password

Kafka Credentials

Supabase Keys

Production secrets stored in a secure secret manager.

---

# 23.23 Logging Security

Never log

Passwords

JWT

Refresh Tokens

Secret Keys

OTP Codes

Credit Card Data (Future)

Sensitive values must be masked.

---

# 23.24 Audit Logging

Audit

Authentication

Authorization

Role Changes

Account Changes

Group Administration

Security Events

Audit logs are immutable.

---

# 23.25 Brute Force Protection

Protect

Login

Registration

Password Reset

OTP

Temporary account lock after repeated failures.

---

# 23.26 Session Security

Support

Logout

Logout All Devices

Refresh Token Revocation

Device Revocation

Inactive sessions expire automatically.

---

# 23.27 Security Headers

Recommended

```
Content-Security-Policy

Strict-Transport-Security

X-Content-Type-Options

Referrer-Policy

Permissions-Policy

X-Frame-Options
```

---

# 23.28 Dependency Security

Regularly scan

Spring Boot

Gradle Dependencies

Docker Images

Operating System Packages

Apply security updates promptly.

---

# 23.29 Monitoring

Monitor

Failed Logins

403 Errors

401 Errors

Rate Limits

JWT Failures

Kafka Authentication

Redis Authentication

Mongo Authentication

Security alerts should trigger notifications.

---

# 23.30 Incident Response

Detect

↓

Contain

↓

Investigate

↓

Recover

↓

Review

Every security incident should be documented.

---

# 23.31 Engineering Rules

Security must

- Validate every request
- Authenticate every user
- Authorize every action
- Hash passwords
- Rotate refresh tokens
- Encrypt communication
- Log security events
- Protect secrets

Must Not

- Trust client input
- Store plaintext passwords
- Expose secrets
- Skip authorization
- Disable validation

---

# 23.32 Definition of Done

Security architecture is complete when

✓ JWT authentication implemented

✓ Refresh token rotation implemented

✓ Argon2id password hashing

✓ Device management implemented

✓ Authorization enforced

✓ Validation implemented

✓ HTTPS configured

✓ Rate limiting enabled

✓ Audit logging enabled

✓ Secrets externalized

✓ Dependency scanning configured

✓ Production-ready

---

# 23.33 Chapter Summary

Duralap follows a defense-in-depth security architecture that combines strong authentication, fine-grained authorization, secure communication, strict validation, immutable audit logging, and continuous monitoring. By applying security at every layer—from HTTP and WebSocket to Kafka, Redis, MongoDB, and infrastructure—the platform is designed to protect user data, resist common attack vectors, and remain resilient in production.



# Chapter 24 - Monitoring & Observability

---

# Duralap Engineering Bible

## Chapter 24

**Monitoring & Observability**

Version: 1.0

Status: Approved

---

# 24.1 Purpose

This chapter defines the monitoring and observability architecture of Duralap.

Observability enables engineers to understand the health, performance, and behavior of the system in production.

Goals

- Detect failures quickly
- Diagnose problems efficiently
- Measure performance
- Improve reliability
- Support incident response

---

# 24.2 Observability Pillars

Duralap follows the three pillars of observability.

```
Metrics

Logs

Traces
```

Together they provide complete visibility into system behavior.

---

# 24.3 Monitoring Stack

Current

```
Spring Boot Actuator

Micrometer

Prometheus

Grafana
```

Logging

```
Logback

JSON Logs
```

Tracing

```
OpenTelemetry

Micrometer Tracing
```

Future

```
Jaeger

Tempo

Zipkin
```

---

# 24.4 Health Checks

Expose

```
/actuator/health
```

Health includes

- MongoDB
- Redis
- Kafka
- Storage
- Disk
- Memory

Example

```
UP

DOWN

DEGRADED
```

---

# 24.5 Metrics

Collect

Application

- HTTP Requests
- Response Time
- Error Rate

Database

- Query Time
- Connection Pool

Redis

- Cache Hit Ratio
- Cache Miss Ratio

Kafka

- Publish Rate
- Consumer Lag

WebSocket

- Active Connections
- Events Sent

JVM

- CPU
- Memory
- Threads
- Garbage Collection

---

# 24.6 HTTP Metrics

Track

- Request Count
- Response Time
- Status Codes
- Endpoint Latency
- Active Requests

Example

```
GET /messages

200

145 ms
```

---

# 24.7 Business Metrics

Track

- Registered Users
- Daily Active Users
- Messages Sent
- Groups Created
- Media Uploads
- Notifications Delivered

Business metrics should not expose personal data.

---

# 24.8 Logging

Every log contains

- Timestamp
- Level
- Service
- Request ID
- Correlation ID
- User ID (if authenticated)
- Thread
- Message

Logs use structured JSON format.

---

# 24.9 Log Levels

```
TRACE

DEBUG

INFO

WARN

ERROR
```

Production

Default

```
INFO
```

Avoid DEBUG logging in production.

---

# 24.10 Correlation IDs

Every request has

```
X-Correlation-ID
```

The same ID propagates through

HTTP

↓

Kafka

↓

WebSocket

↓

Logs

↓

Monitoring

This enables end-to-end tracing.

---

# 24.11 Distributed Tracing

Every request creates a trace.

Example

```
HTTP Request

↓

User Service

↓

MongoDB

↓

Kafka

↓

Notification Service

↓

WebSocket
```

Entire request path becomes traceable.

---

# 24.12 Alerting

Alerts for

- High Error Rate
- High CPU
- High Memory
- Mongo Down
- Redis Down
- Kafka Down
- Storage Down
- Consumer Lag
- High Response Time

Alerts should notify operators immediately.

---

# 24.13 Dashboards

Create dashboards for

Application

Infrastructure

MongoDB

Redis

Kafka

WebSocket

Security

Business Metrics

---

# 24.14 JVM Monitoring

Track

- Heap Usage
- Non-Heap Usage
- GC Pause Time
- Thread Count
- Thread Deadlocks
- Class Loading

---

# 24.15 Database Monitoring

MongoDB

Track

- Query Latency
- Slow Queries
- Index Usage
- Connection Pool
- Storage Growth

---

# 24.16 Redis Monitoring

Track

- Memory Usage
- Cache Hit Rate
- Evictions
- Expired Keys
- Connected Clients

---

# 24.17 Kafka Monitoring

Track

- Topic Throughput
- Publish Rate
- Consumer Lag
- Retry Count
- DLQ Size

---

# 24.18 WebSocket Monitoring

Track

- Active Connections
- Connection Failures
- Disconnects
- Reconnects
- Messages Delivered
- Typing Events
- Presence Updates

---

# 24.19 Security Monitoring

Track

- Failed Logins
- Invalid JWT
- Rate Limits
- Permission Denials
- Suspicious Activity

Security events should be auditable.

---

# 24.20 Incident Response

Detect

↓

Alert

↓

Investigate

↓

Mitigate

↓

Recover

↓

Postmortem

Every production incident should have a documented postmortem.

---

# 24.21 Performance Targets

API

95th Percentile

< 300 ms

WebSocket Event

< 100 ms

Kafka Consumer Lag

< 5 sec

Mongo Query

< 100 ms

Redis

< 5 ms

---

# 24.22 Log Retention

Application Logs

30 Days

Audit Logs

1 Year (configurable)

Metrics

90 Days

Traces

7 Days

Retention policies should be configurable.

---

# 24.23 Engineering Rules

Monitoring must

- Cover every module
- Use structured logs
- Propagate correlation IDs
- Expose health endpoints
- Collect metrics
- Support tracing

Must Not

- Log passwords
- Log JWTs
- Log secrets
- Log sensitive personal data

---

# 24.24 Definition of Done

Monitoring is complete when

✓ Health endpoints implemented

✓ Metrics exposed

✓ Prometheus configured

✓ Grafana dashboards created

✓ Structured logging enabled

✓ Correlation IDs propagated

✓ Distributed tracing enabled

✓ Alerts configured

✓ JVM monitoring enabled

✓ Kafka monitoring enabled

✓ Redis monitoring enabled

✓ Mongo monitoring enabled

✓ Production-ready

---

# 24.25 Chapter Summary

Duralap's monitoring and observability architecture provides complete visibility into application health, infrastructure, performance, and business behavior. Through metrics, logs, traces, dashboards, and automated alerting, engineers can rapidly detect, diagnose, and resolve issues while maintaining high availability and operational excellence.



# Chapter 25 - Deployment Architecture

---

# Duralap Engineering Bible

## Chapter 25

**Deployment Architecture**

Version: 1.0

Status: Approved

---

# 25.1 Purpose

This chapter defines the deployment architecture for Duralap.

It covers:

- Local Development
- Testing
- Staging
- Production
- CI/CD
- Scaling
- Disaster Recovery

The deployment architecture ensures Duralap can evolve from a single developer environment into an enterprise-scale platform.

---

# 25.2 Deployment Principles

The deployment architecture follows:

- Infrastructure as Code
- Immutable Deployments
- Zero Downtime Deployments
- Automated CI/CD
- Secure Configuration
- Horizontal Scalability
- High Availability

---

# 25.3 Deployment Environments

Development

Purpose

Local developer machines.

Staging

Purpose

Production-like testing.

Production

Purpose

Real users.

Each environment has isolated resources.

---

# 25.4 Environment Configuration

Environment-specific configuration is externalized.

Examples

```
application-local.yml

application-dev.yml

application-staging.yml

application-prod.yml
```

Never hardcode secrets.

---

# 25.5 Local Development

Runs using

```
Spring Boot

MongoDB

Redis

Kafka

Supabase Storage

Docker Compose
```

Developers should be able to start the entire stack with one command.

---

# 25.6 Docker

Every backend service must provide:

```
Dockerfile
```

Requirements

- Multi-stage build
- Small runtime image
- Non-root user
- Health check
- JVM tuning

---

# 25.7 Docker Compose

Local development includes

```
Backend

MongoDB

Redis

Kafka

Kafka UI

Mongo Express (optional)
```

Docker Compose is intended for development only.

---

# 25.8 Container Registry

Images are pushed to

Future

```
GitHub Container Registry

Docker Hub

Amazon ECR
```

Images are versioned using Git tags.

---

# 25.9 Reverse Proxy

Production uses

```
Nginx
```

Responsibilities

- HTTPS
- Compression
- Static file serving (if applicable)
- Reverse proxy
- Rate limiting (optional)

---

# 25.10 HTTPS

All production traffic uses

TLS 1.3 preferred

Certificates

Let's Encrypt or managed certificates.

HTTP redirects to HTTPS.

---

# 25.11 Scaling Strategy

Backend

Horizontal scaling.

MongoDB

Replica Set

Redis

Sentinel

Kafka

Multiple brokers

Application instances remain stateless.

---

# 25.12 Load Balancer

Production architecture

```
Internet

↓

Load Balancer

↓

Backend Instance A

Backend Instance B

Backend Instance C
```

Sessions are not stored in application memory.

---

# 25.13 WebSocket Scaling

WebSocket sessions are coordinated through

Redis Pub/Sub

This enables multiple backend instances to deliver events consistently.

---

# 25.14 Storage

Media files

↓

Supabase Storage

Application stores only metadata in MongoDB.

---

# 25.15 CI/CD Pipeline

Pipeline

```
Git Push

↓

Build

↓

Static Analysis

↓

Unit Tests

↓

Integration Tests

↓

Security Scan

↓

Docker Build

↓

Publish Image

↓

Deploy Staging

↓

Manual Approval

↓

Deploy Production
```

Every stage must pass before deployment.

---

# 25.16 GitHub Actions

Primary CI/CD platform

Responsibilities

- Build
- Test
- Lint
- Security Scan
- Docker Build
- Publish
- Deployment

---

# 25.17 Secrets Management

Secrets stored outside Git.

Examples

```
JWT Secret

Mongo URI

Redis Password

Kafka Credentials

Supabase Keys
```

Production uses a secure secrets manager.

---

# 25.18 Database Deployment

MongoDB

Production

Replica Set

Backups

Daily

Restore tests

Monthly

---

# 25.19 Redis Deployment

Production

Redis Sentinel

Future

Redis Cluster

Persistence is optional depending on runtime requirements.

---

# 25.20 Kafka Deployment

Production

Multiple Brokers

Replication Factor

3

Min In-Sync Replicas

2

DLQ topics configured.

---

# 25.21 Logging

Logs are centralized.

Current

Console + File

Future

ELK Stack

OpenSearch

Grafana Loki

---

# 25.22 Monitoring

Production includes

Prometheus

Grafana

Spring Boot Actuator

OpenTelemetry

Health checks are continuously monitored.

---

# 25.23 Backup Strategy

MongoDB

Daily Backup

Redis

No backup required for cache

Kafka

Topic retention configured

Supabase

Managed backup

Backups are encrypted.

---

# 25.24 Disaster Recovery

Recovery process

```
Detect

↓

Restore Database

↓

Restore Storage

↓

Verify Services

↓

Resume Traffic
```

Recovery procedures are documented and tested.

---

# 25.25 Rolling Deployments

Deployment

```
Instance A

↓

Deploy

↓

Health Check

↓

Traffic Enabled

↓

Repeat
```

No downtime for users.

---

# 25.26 Rollback Strategy

Rollback occurs when

- Health checks fail
- Error rate increases
- Deployment verification fails

Rollback restores the previous stable version.

---

# 25.27 Performance Targets

Application Startup

< 30 sec

Health Check

< 5 sec

Deployment

< 10 min

Rollback

< 5 min

---

# 25.28 Engineering Rules

Deployment must

- Be automated
- Be reproducible
- Use Docker
- Be stateless
- Externalize configuration
- Support scaling
- Support rollback

Must Not

- Store secrets in Git
- Require manual server configuration
- Depend on local state
- Skip automated testing

---

# 25.29 Definition of Done

Deployment architecture is complete when

✓ Dockerfiles created

✓ Docker Compose configured

✓ GitHub Actions pipeline implemented

✓ Secrets externalized

✓ HTTPS configured

✓ Monitoring integrated

✓ Backup strategy implemented

✓ Rolling deployment supported

✓ Rollback strategy documented

✓ Production-ready

---

# 25.30 Chapter Summary

Duralap's deployment architecture enables reliable, secure, and scalable operation across development, staging, and production environments. Through containerization, automated CI/CD, stateless services, secure configuration, monitoring, and disaster recovery planning, the platform is prepared for enterprise-grade deployments while remaining easy to develop and maintain.


# Chapter 26 - Docker & Infrastructure

---

# Duralap Engineering Bible

## Chapter 26

**Docker & Infrastructure**

Version: 1.0

Status: Approved

---

# 26.1 Purpose

This chapter defines the containerization and infrastructure architecture of Duralap.

Goals

- Reproducible environments
- Infrastructure as Code
- Easy onboarding
- Consistent deployments
- Horizontal scalability
- Production readiness

Every service must run inside containers.

---

# 26.2 Infrastructure Philosophy

Infrastructure should be

- Immutable
- Automated
- Reproducible
- Portable
- Version Controlled
- Secure

Manual server configuration is prohibited.

---

# 26.3 Containerized Services

Development stack

```
Spring Boot

MongoDB

Redis

Apache Kafka

Kafka UI

Mongo Express (Optional)
```

Future

```
Prometheus

Grafana

Loki

Tempo

Nginx
```

---

# 26.4 Infrastructure Overview

```
Clients
      │
      ▼
Nginx Reverse Proxy
      │
      ▼
Spring Boot API
      │
 ┌────┼────┐
 ▼    ▼    ▼
Mongo Redis Kafka
      │
      ▼
Supabase Storage
```

---

# 26.5 Repository Structure

```
docker/

├── compose/
│   ├── docker-compose.local.yml
│   ├── docker-compose.dev.yml
│   ├── docker-compose.staging.yml
│   └── docker-compose.prod.yml
│
├── mongo/
│
├── redis/
│
├── kafka/
│
├── nginx/
│
├── monitoring/
│
└── scripts/
```

Infrastructure remains isolated from application code.

---

# 26.6 Dockerfile Standards

Every service must have

```
Dockerfile
```

Requirements

- Multi-stage build
- Non-root user
- Health check
- Small runtime image
- JVM optimization

---

# 26.7 Base Image

Preferred

```
Eclipse Temurin 21 JRE
```

Build image

```
Gradle + JDK 21
```

Avoid unnecessary packages.

---

# 26.8 Docker Build Rules

Build process

```
Source

↓

Gradle Build

↓

Jar

↓

Runtime Image

↓

Deploy
```

Build cache should be utilized.

---

# 26.9 Container Naming

Examples

```
duralap-api

duralap-mongo

duralap-redis

duralap-kafka

duralap-nginx
```

Use consistent naming across environments.

---

# 26.10 Networking

Create dedicated Docker networks.

```
frontend-network

backend-network
```

Services communicate through private networks.

No unnecessary ports are exposed.

---

# 26.11 Volumes

Persistent volumes

```
MongoDB

Kafka

Redis (optional)

Logs
```

Application containers remain stateless.

---

# 26.12 Environment Variables

Examples

```
SPRING_PROFILES_ACTIVE

MONGO_URI

REDIS_HOST

REDIS_PORT

KAFKA_BOOTSTRAP_SERVERS

SUPABASE_URL

SUPABASE_KEY

JWT_SECRET
```

Secrets are never committed to Git.

---

# 26.13 Startup Order

Services start in order

```
MongoDB

↓

Redis

↓

Kafka

↓

Spring Boot

↓

Nginx
```

Readiness checks ensure dependencies are available.

---

# 26.14 Health Checks

Every container exposes a health endpoint.

Spring Boot

```
/actuator/health
```

Docker waits for healthy dependencies before starting dependent services.

---

# 26.15 Resource Limits

Configure

- CPU
- Memory
- Restart Policy

Example

```
restart: unless-stopped
```

Prevent resource exhaustion.

---

# 26.16 Logging

Container logs use

```
stdout

stderr
```

Application logs remain structured JSON.

Future

Centralized logging.

---

# 26.17 Image Versioning

Version format

```
v1.0.0

v1.1.0

v2.0.0
```

Latest tag should not be used in production.

---

# 26.18 Security

Containers must

- Run as non-root
- Use minimal base images
- Remove build tools from runtime image
- Scan images regularly

Only required ports are exposed.

---

# 26.19 Secrets

Use

```
.env

Docker Secrets (Future)

External Secret Manager (Production)
```

Never bake secrets into Docker images.

---

# 26.20 Infrastructure Monitoring

Monitor

- Container health
- CPU
- Memory
- Disk
- Restart count
- Network traffic

Metrics integrate with Prometheus.

---

# 26.21 Backup

Persistent data

- MongoDB
- Kafka
- Configuration

Containers themselves are disposable.

---

# 26.22 Scaling

Application containers

```
Scale Horizontally
```

Mongo

```
Replica Set
```

Redis

```
Sentinel
```

Kafka

```
Multi-Broker Cluster
```

---

# 26.23 Local Development

Developers start the entire stack with

```
docker compose up
```

One command should start the complete development environment.

---

# 26.24 Production Infrastructure

Production includes

- HTTPS
- Reverse Proxy
- Monitoring
- Backups
- Rolling Deployments
- Centralized Logs

Production differs only in configuration, not architecture.

---

# 26.25 Disaster Recovery

Recovery

```
Restore Volumes

↓

Restore MongoDB

↓

Restore Kafka

↓

Verify Redis

↓

Start Services

↓

Health Check
```

Recovery procedures should be documented and tested.

---

# 26.26 Engineering Rules

Infrastructure must

- Be version controlled
- Use Docker
- Use Compose for local development
- Externalize configuration
- Support health checks
- Support scaling

Must Not

- Store secrets in images
- Require manual setup
- Depend on host configuration
- Use mutable containers

---

# 26.27 Definition of Done

Docker infrastructure is complete when

✓ Dockerfiles implemented

✓ Docker Compose configured

✓ Health checks enabled

✓ Persistent volumes configured

✓ Environment variables externalized

✓ Container networking configured

✓ Logging standardized

✓ Monitoring integrated

✓ Security hardened

✓ Production-ready

---

# 26.28 Chapter Summary

Duralap's Docker and infrastructure architecture provides a reproducible, portable, and scalable foundation for development and production. Through containerization, standardized images, isolated networking, persistent storage, health checks, and Infrastructure as Code principles, the platform ensures consistency across every environment while simplifying deployment, scaling, and maintenance.


# Chapter 27 - Testing Strategy

---

# Duralap Engineering Bible

## Chapter 27

**Testing Strategy**

Version: 1.0

Status: Approved

---

# 27.1 Purpose

This chapter defines the testing strategy for Duralap.

Goals

- Ensure correctness
- Prevent regressions
- Verify architecture
- Improve reliability
- Support continuous delivery

Every feature must be tested before deployment.

---

# 27.2 Testing Philosophy

Testing follows these principles

- Test behavior, not implementation
- Automate whenever possible
- Keep tests deterministic
- Keep tests independent
- Fail fast
- Test at multiple layers

Manual testing alone is never sufficient.

---

# 27.3 Testing Pyramid

```
               E2E Tests
             Integration Tests
                Unit Tests
```

Target distribution

```
70% Unit Tests

20% Integration Tests

10% End-to-End Tests
```

---

# 27.4 Test Types

Unit Tests

Integration Tests

API Tests

Repository Tests

Security Tests

Kafka Tests

Redis Tests

WebSocket Tests

Performance Tests

Load Tests

End-to-End Tests

Smoke Tests

Regression Tests

---

# 27.5 Unit Testing

Purpose

Verify business logic.

Test

- Services
- Use Cases
- Validators
- Mappers
- Utilities

Never test frameworks.

Mock external dependencies.

---

# 27.6 Integration Testing

Verify collaboration between components.

Examples

Spring Boot

↓

MongoDB

↓

Redis

↓

Kafka

↓

Storage

Use Testcontainers whenever possible.

---

# 27.7 Repository Testing

Verify

- CRUD
- Queries
- Index usage
- Pagination
- Transactions

Run against real MongoDB using Testcontainers.

---

# 27.8 Controller Testing

Verify

- HTTP Status
- Request Validation
- Response DTOs
- Error Handling
- Authentication

Use MockMvc or WebTestClient.

---

# 27.9 API Testing

Verify

- Endpoints
- Pagination
- Filtering
- Sorting
- Validation
- Authorization

Every public API must be covered.

---

# 27.10 Security Testing

Verify

- JWT
- Refresh Tokens
- Authorization
- Ownership
- Role Checks
- Rate Limiting

Unauthorized requests must fail.

---

# 27.11 Kafka Testing

Verify

- Producer
- Consumer
- Retry
- DLQ
- Ordering
- Serialization
- Idempotency

Use embedded Kafka or Testcontainers.

---

# 27.12 Redis Testing

Verify

- Cache Hit
- Cache Miss
- TTL
- Invalidation
- Presence
- Typing Indicators

Redis failures should degrade gracefully.

---

# 27.13 WebSocket Testing

Verify

- Authentication
- Connection
- Subscription
- Messaging
- Presence
- Typing
- Read Receipts
- Delivery Receipts
- Reconnection

---

# 27.14 Media Testing

Verify

- Upload
- Download
- Metadata
- Validation
- File Limits

Large file uploads should be tested.

---

# 27.15 Performance Testing

Measure

- API Latency
- Mongo Queries
- Redis Operations
- Kafka Throughput
- WebSocket Delivery

Performance regressions should fail CI.

---

# 27.16 Load Testing

Simulate

- Thousands of users
- Concurrent messaging
- Group messaging
- Presence updates

Measure

- CPU
- Memory
- Latency
- Throughput

---

# 27.17 End-to-End Testing

Validate complete user journeys.

Examples

Register

↓

Login

↓

Create Conversation

↓

Send Message

↓

Receive Message

↓

Read Receipt

↓

Logout

---

# 27.18 Smoke Testing

Executed after deployment.

Verify

- Application starts
- Database connected
- Redis connected
- Kafka connected
- Health endpoint
- Authentication

---

# 27.19 Regression Testing

Run on every pull request.

Ensure existing functionality remains unchanged.

---

# 27.20 Test Data

Use isolated test data.

Never use production data.

Reset state after every test.

---

# 27.21 Code Coverage

Minimum targets

Unit Tests

80%

Service Layer

90%

Critical Modules

95%

Coverage is a guideline, not the sole measure of quality.

---

# 27.22 Continuous Integration

Every pull request runs

Build

↓

Lint

↓

Unit Tests

↓

Integration Tests

↓

Security Scan

↓

Coverage Report

↓

Docker Build

Merge blocked if pipeline fails.

---

# 27.23 Test Naming

Examples

```
shouldCreateConversation()

shouldRejectExpiredToken()

shouldPublishKafkaEvent()

shouldReconnectWebSocket()

shouldReturn404WhenUserNotFound()
```

Test names describe expected behavior.

---

# 27.24 Testing Tools

Backend

JUnit 5

Mockito

Spring Boot Test

Testcontainers

MockMvc

WebTestClient

Future

Gatling

k6

OWASP ZAP

---

# 27.25 Engineering Rules

Testing must

- Be automated
- Be deterministic
- Be repeatable
- Cover business logic
- Cover security
- Cover integrations

Must Not

- Depend on production services
- Share mutable state
- Require manual setup
- Ignore failing tests

---

# 27.26 Definition of Done

Testing strategy is complete when

✓ Unit tests implemented

✓ Integration tests implemented

✓ Repository tests implemented

✓ API tests implemented

✓ Security tests implemented

✓ Kafka tests implemented

✓ Redis tests implemented

✓ WebSocket tests implemented

✓ Load tests documented

✓ Smoke tests automated

✓ CI pipeline enforces testing

✓ Production-ready

---

# 27.27 Chapter Summary

Duralap's testing strategy combines unit, integration, API, messaging, security, performance, and end-to-end testing to ensure reliability at every layer. Automated testing, continuous integration, and quality gates provide confidence that new features can be delivered safely without breaking existing functionality.



# Chapter 28 - Performance Strategy

---

# Duralap Engineering Bible

## Chapter 28

**Performance Strategy**

Version: 1.0

Status: Approved

---

# 28.1 Purpose

This chapter defines the performance strategy for Duralap.

Goals

- Low latency
- High throughput
- Efficient resource usage
- Horizontal scalability
- Predictable performance
- Excellent user experience

Performance optimization must never compromise correctness or security.

---

# 28.2 Performance Principles

Duralap follows

- Measure before optimizing
- Cache intelligently
- Minimize network calls
- Reduce database load
- Asynchronous processing
- Efficient resource utilization

Optimization must be data-driven.

---

# 28.3 Performance Targets

REST API (P95)

< 300 ms

WebSocket Event

< 100 ms

Redis Query

< 5 ms

MongoDB Query

< 100 ms

Kafka Publish

< 50 ms

Kafka Consume

< 100 ms

Application Startup

< 30 seconds

---

# 28.4 API Performance

Optimize

- Database queries
- Serialization
- DTO mapping
- Validation
- Pagination

Avoid unnecessary processing.

---

# 28.5 Database Performance

MongoDB optimization

- Proper indexes
- Covered queries
- Cursor pagination
- Projection
- Aggregation optimization

Avoid collection scans.

---

# 28.6 Index Strategy

Create indexes for

- User lookup
- Username
- Email
- Conversation members
- Message timestamps
- Group membership
- Notifications

Indexes are reviewed periodically.

---

# 28.7 Query Optimization

Prefer

Indexed queries

Projection

Aggregation pipelines

Avoid

Large document reads

Unbounded queries

N+1 query patterns

---

# 28.8 Redis Performance

Cache

- User profiles
- Conversation metadata
- Presence
- Typing indicators
- Frequently accessed settings

Do not cache highly volatile data without a clear invalidation strategy.

---

# 28.9 Cache Strategy

Policies

Cache Aside

Read Through (Future)

Write Through (Future)

Cache entries must have defined TTL values.

---

# 28.10 Kafka Performance

Optimize

- Batch publishing
- Compression
- Consumer concurrency
- Partitioning
- Retry configuration

Maintain message ordering where required.

---

# 28.11 WebSocket Performance

Optimize

- Connection management
- Event batching (where appropriate)
- Heartbeats
- Subscription efficiency
- Payload size

Minimize unnecessary broadcasts.

---

# 28.12 Payload Optimization

Responses should

- Return only required fields
- Compress when appropriate
- Avoid redundant metadata

DTOs should remain lightweight.

---

# 28.13 Pagination

Use

Cursor-based pagination

Default

20

Maximum

100

Never return unbounded collections.

---

# 28.14 Background Processing

Move long-running tasks to asynchronous processing.

Examples

- Notification delivery
- Media processing
- Analytics
- Email sending

REST APIs should remain responsive.

---

# 28.15 Connection Pooling

Optimize pools for

MongoDB

Redis

Kafka

HTTP Clients

Prevent resource exhaustion.

---

# 28.16 JVM Optimization

Monitor

- Heap usage
- Garbage collection
- Thread count
- CPU utilization

Tune JVM based on production metrics.

---

# 28.17 Memory Management

Avoid

- Memory leaks
- Large object retention
- Unnecessary object creation

Use immutable DTOs where practical.

---

# 28.18 Thread Management

Avoid blocking operations.

Use asynchronous execution for

- Kafka consumers
- Background jobs
- External service calls

Thread pools must be configured explicitly.

---

# 28.19 File Performance

Media uploads

- Stream files
- Avoid loading entire files into memory
- Validate before upload

Large files should use chunked upload (Future).

---

# 28.20 Network Optimization

Use

- HTTPS compression
- HTTP Keep-Alive
- Efficient JSON payloads

Reduce unnecessary round trips.

---

# 28.21 Monitoring Performance

Track

- API latency
- Database latency
- Cache hit ratio
- Kafka throughput
- WebSocket latency
- JVM metrics

Performance metrics feed dashboards and alerts.

---

# 28.22 Load Testing

Regularly simulate

- High API traffic
- Concurrent messaging
- Large groups
- Media uploads
- Burst notifications

Measure latency and throughput.

---

# 28.23 Performance Regression

Every release should compare

- Response times
- Memory usage
- CPU usage
- Throughput

Performance regressions must be investigated before release.

---

# 28.24 Engineering Rules

Performance optimization must

- Be measurable
- Preserve correctness
- Preserve security
- Avoid premature optimization
- Be continuously monitored

Must Not

- Sacrifice readability without justification
- Remove validation
- Bypass authorization
- Introduce inconsistent caching

---

# 28.25 Definition of Done

Performance strategy is complete when

✓ API latency targets met

✓ Database indexes optimized

✓ Redis cache configured

✓ Kafka tuned

✓ WebSocket optimized

✓ Monitoring dashboards available

✓ Load tests executed

✓ JVM tuned

✓ Performance regression checks automated

✓ Production-ready

---

# 28.26 Chapter Summary

Duralap's performance strategy focuses on delivering fast, reliable, and scalable user experiences through efficient database access, intelligent caching, optimized messaging, asynchronous processing, and continuous performance monitoring. Performance is treated as an ongoing engineering discipline rather than a one-time optimization effort.



# Chapter 29 - Coding Standards

---

# Duralap Engineering Bible

## Chapter 29

**Coding Standards**

Version: 1.0

Status: Approved

---

# 29.1 Purpose

This chapter defines the coding standards for Duralap.

Goals

- Readability
- Consistency
- Maintainability
- Simplicity
- Testability
- Scalability

Every developer follows the same standards.

---

# 29.2 Core Principles

Every piece of code should follow

- Clean Code
- SOLID
- DRY
- KISS
- YAGNI
- Composition over Inheritance
- Explicit over Implicit

Readable code is preferred over clever code.

---

# 29.3 Package Structure

Follow Modular Monolith architecture.

```
identity/

user/

conversation/

message/

group/

media/

notification/

search/

common/
```

Cross-module dependencies must be minimized.

---

# 29.4 Class Responsibilities

Each class should have a single responsibility.

Examples

```
Controller

Service

Repository

Mapper

Validator

Configuration
```

Avoid "God Classes".

---

# 29.5 Naming Conventions

Classes

```
UserService

MessageController

ConversationRepository
```

Interfaces

```
MessageService
```

Implementations

```
MessageServiceImpl
```

DTOs

```
CreateMessageRequest

MessageResponse
```

Enums

```
ConversationType

UserStatus
```

Constants

```
MAX_MESSAGE_LENGTH
```

---

# 29.6 Method Design

Methods should

- Perform one task
- Have descriptive names
- Avoid side effects
- Return predictable results

Prefer

```
createConversation()

sendMessage()

markAsRead()
```

Avoid

```
doStuff()

process()

handle()
```

---

# 29.7 Method Size

Target

```
< 30 lines
```

Extract private methods when logic becomes complex.

---

# 29.8 Class Size

Target

```
< 300 lines
```

Split large classes into smaller components.

---

# 29.9 Dependency Injection

Use constructor injection.

Example

```
@Service
class UserService(
    private val userRepository: UserRepository
)
```

Avoid field injection.

---

# 29.10 Controllers

Controllers should

- Validate requests
- Call services
- Return DTOs
- Map HTTP responses

Controllers must not contain business logic.

---

# 29.11 Services

Services contain

- Business rules
- Transactions
- Domain validation
- Event publishing

Services coordinate repositories.

---

# 29.12 Repositories

Repositories

- Access MongoDB
- Execute queries
- Return domain objects

Repositories must not contain business logic.

---

# 29.13 DTOs

Use separate DTOs for

Request

Response

Events

Never expose entities directly.

---

# 29.14 Entity Rules

Entities represent domain models.

Entities should

- Contain domain state
- Avoid framework-specific logic
- Remain persistence-focused

---

# 29.15 Validation

Use Bean Validation.

Examples

```
@NotBlank

@Email

@Size

@Pattern
```

Validation belongs in DTOs.

---

# 29.16 Exception Handling

Throw custom exceptions.

Examples

```
UserNotFoundException

ConversationNotFoundException

AccessDeniedException
```

Never throw generic Exception.

---

# 29.17 Logging

Log

- Business events
- Errors
- Warnings

Do not log

- Passwords
- Tokens
- Secrets

---

# 29.18 Kotlin Standards

Prefer

- data class
- val over var
- sealed class
- nullable safety
- extension functions

Avoid unnecessary mutable state.

---

# 29.19 Coroutines

Use Kotlin Coroutines where appropriate.

Avoid blocking operations.

Background tasks should be asynchronous.

---

# 29.20 Null Safety

Prefer

```
?

?: 

requireNotNull()
```

Avoid

```
!!
```

unless absolutely necessary.

---

# 29.21 Constants

Shared constants belong in dedicated objects.

Example

```
object ApiConstants
```

Avoid magic numbers.

---

# 29.22 Comments

Comments explain

WHY

not

WHAT

Prefer self-explanatory code.

---

# 29.23 Formatting

Use

- 4-space indentation
- UTF-8 encoding
- Unix line endings

Automated formatting should be enforced.

---

# 29.24 Static Analysis

Use

- ktlint
- Detekt

Build fails on critical violations.

---

# 29.25 Documentation

Public APIs

Services

Configurations

Complex algorithms

should be documented.

---

# 29.26 Code Reviews

Every pull request requires review.

Review checklist

- Architecture
- Readability
- Security
- Performance
- Tests
- Documentation

---

# 29.27 Engineering Rules

Code must

- Compile cleanly
- Pass all tests
- Follow module boundaries
- Use DTOs
- Use constructor injection
- Be covered by tests

Must Not

- Contain dead code
- Duplicate logic
- Expose entities
- Use hardcoded secrets
- Bypass validation

---

# 29.28 Definition of Done

Coding standards are complete when

✓ Style guide documented

✓ Formatting automated

✓ Static analysis enabled

✓ Code review process established

✓ Module boundaries enforced

✓ DTO usage standardized

✓ Constructor injection enforced

✓ Documentation guidelines defined

✓ Production-ready

---

# 29.29 Chapter Summary

Duralap's coding standards establish a consistent and maintainable codebase through clean architecture, SOLID principles, Kotlin best practices, standardized naming, constructor injection, DTO-based communication, and automated quality checks. These standards ensure that every contributor writes code that is readable, testable, secure, and aligned with the project's long-term architecture.


# Chapter 30 - Git Workflow

---

# Duralap Engineering Bible

## Chapter 30

**Git Workflow**

Version: 1.0

Status: Approved

---

# 30.1 Purpose

This chapter defines the Git workflow for Duralap.

Goals

- Consistent collaboration
- Safe development
- Clean commit history
- Reliable releases
- Traceable changes
- Easy rollback

Every code change must follow this workflow.

---

# 30.2 Branching Strategy

Main branches

```
main

develop
```

Feature branches

```
feature/<feature-name>
```

Bug fixes

```
fix/<issue-name>
```

Hotfixes

```
hotfix/<issue-name>
```

Documentation

```
docs/<topic>
```

Refactoring

```
refactor/<module>
```

Research

```
spike/<topic>
```

---

# 30.3 Branch Responsibilities

main

Production-ready code only.

develop

Integration branch for upcoming release.

feature/*

New features.

fix/*

Bug fixes.

hotfix/*

Urgent production fixes.

docs/*

Documentation updates.

refactor/*

Code improvements without feature changes.

---

# 30.4 Branch Lifecycle

```
develop

↓

feature/message-search

↓

Commit

↓

Push

↓

Pull Request

↓

Review

↓

Merge into develop

↓

Release

↓

main
```

---

# 30.5 Commit Strategy

Commits should

- Be small
- Be atomic
- Build successfully
- Pass tests

Avoid large "everything changed" commits.

---

# 30.6 Commit Message Convention

Format

```
type(scope): short description
```

Examples

```
feat(identity): implement refresh token rotation

fix(message): prevent duplicate delivery

refactor(conversation): simplify service layer

docs(api): update authentication examples

test(user): add integration tests

perf(redis): optimize presence cache

chore(deps): upgrade Spring Boot
```

---

# 30.7 Commit Types

```
feat

fix

refactor

docs

test

perf

style

build

ci

chore

revert
```

---

# 30.8 Pull Requests

Every change requires a Pull Request.

PR description includes

- Purpose
- Summary
- Screenshots (if applicable)
- Testing performed
- Breaking changes
- Related issue

---

# 30.9 Pull Request Checklist

Before requesting review

✓ Builds successfully

✓ Tests pass

✓ ktlint passes

✓ Detekt passes

✓ Documentation updated

✓ No secrets committed

✓ Architecture respected

✓ API updated if required

---

# 30.10 Code Review

Every PR is reviewed for

- Architecture
- Security
- Performance
- Readability
- Tests
- Documentation

Reviewers should explain requested changes.

---

# 30.11 Merge Strategy

Preferred

```
Squash and Merge
```

Benefits

- Clean history
- One commit per feature
- Easier rollback

Avoid merge commits unless required.

---

# 30.12 Release Process

```
develop

↓

Release Candidate

↓

Testing

↓

Approval

↓

Merge into main

↓

Tag

↓

Deploy
```

---

# 30.13 Versioning

Semantic Versioning

```
MAJOR.MINOR.PATCH
```

Examples

```
1.0.0

1.1.0

1.1.1

2.0.0
```

---

# 30.14 Git Tags

Every production release receives a tag.

Examples

```
v1.0.0

v1.1.0

v1.2.0
```

Tags are immutable.

---

# 30.15 Release Notes

Each release documents

- New features
- Improvements
- Bug fixes
- Breaking changes
- Database changes
- Known issues

---

# 30.16 Issue Tracking

Every significant change links to an issue.

Examples

```
DLP-145

Fixes #42

Closes #98
```

---

# 30.17 Conflict Resolution

Resolve conflicts

- On feature branch
- Before review
- After syncing with develop

Never resolve conflicts directly on main.

---

# 30.18 Protected Branches

Protect

```
main

develop
```

Rules

- No force push
- No direct commits
- PR required
- Status checks required
- Reviews required

---

# 30.19 CI Requirements

Every PR executes

Build

↓

Formatting

↓

Static Analysis

↓

Unit Tests

↓

Integration Tests

↓

Security Scan

↓

Coverage Report

↓

Docker Build

Merge blocked if any step fails.

---

# 30.20 Rollback

Rollback uses

```
git revert
```

Avoid rewriting shared history.

Never use force push on protected branches.

---

# 30.21 Git Ignore

Ignore

```
build/

.gradle/

.idea/

.env

logs/

*.iml

*.log
```

Do not commit generated files.

---

# 30.22 Large Files

Do not store

- Videos
- Images
- Database dumps
- Build artifacts

Use object storage or Git LFS if necessary.

---

# 30.23 Security

Never commit

- JWT secrets
- API keys
- Database passwords
- Redis credentials
- Kafka credentials
- Supabase keys

Secrets belong in environment configuration.

---

# 30.24 Engineering Rules

Git workflow must

- Keep history clean
- Require reviews
- Require CI
- Use feature branches
- Tag releases

Must Not

- Commit directly to main
- Commit secrets
- Skip reviews
- Merge failing builds
- Force push protected branches

---

# 30.25 Definition of Done

Git workflow is complete when

✓ Branch strategy documented

✓ Commit convention adopted

✓ PR template created

✓ Review process defined

✓ CI integrated

✓ Protected branches configured

✓ Semantic versioning adopted

✓ Release process documented

✓ Production-ready

---

# 30.26 Chapter Summary

Duralap's Git workflow ensures that every code change is traceable, reviewed, tested, and safely integrated. By combining feature branches, pull requests, semantic versioning, automated CI, and protected branches, the project maintains a clean history and a reliable release process suitable for long-term enterprise development.


# Chapter 31 - CI/CD Pipeline

---

# Duralap Engineering Bible

## Chapter 31

**Continuous Integration & Continuous Deployment (CI/CD)**

Version: 1.0

Status: Approved

---

# 31.1 Purpose

This chapter defines the Continuous Integration and Continuous Deployment (CI/CD) strategy for Duralap.

Goals

- Fully automated builds
- Reliable deployments
- High code quality
- Fast feedback
- Safe releases
- Easy rollback

Every code change must pass the CI/CD pipeline before deployment.

---

# 31.2 CI/CD Philosophy

Duralap follows these principles

- Everything is automated
- Every commit is verified
- Deployments are repeatable
- Infrastructure is version-controlled
- Rollbacks are simple
- Releases are traceable

Manual deployments are discouraged.

---

# 31.3 Pipeline Overview

```
Developer

↓

Git Push

↓

GitHub Actions

↓

Build

↓

Static Analysis

↓

Unit Tests

↓

Integration Tests

↓

Security Scan

↓

Coverage Report

↓

Docker Build

↓

Publish Image

↓

Deploy Staging

↓

Manual Approval

↓

Deploy Production
```

---

# 31.4 Pipeline Stages

1. Checkout Source

2. Dependency Cache

3. Build

4. Lint

5. Static Analysis

6. Unit Tests

7. Integration Tests

8. Security Scan

9. Coverage Check

10. Docker Build

11. Push Docker Image

12. Deploy Staging

13. Smoke Test

14. Manual Approval

15. Deploy Production

---

# 31.5 Build Stage

Tasks

- Restore dependencies
- Compile project
- Verify Gradle configuration
- Generate JAR

Build must fail on compilation errors.

---

# 31.6 Code Quality Stage

Run

- ktlint
- Detekt

Build fails on critical violations.

---

# 31.7 Testing Stage

Execute

- Unit Tests
- Integration Tests
- Repository Tests
- API Tests

Tests run in parallel where possible.

---

# 31.8 Security Stage

Perform

- Dependency vulnerability scan
- Secret scanning
- Static security analysis

Critical vulnerabilities block deployment.

---

# 31.9 Code Coverage

Minimum

Overall

80%

Critical modules

90%

Coverage reports are generated automatically.

---

# 31.10 Docker Stage

Build

```
Docker Image
```

Requirements

- Multi-stage build
- Versioned image
- Tagged using Git version

Example

```
duralap-api:v1.0.0
```

---

# 31.11 Container Registry

Images are published to

- GitHub Container Registry
- Docker Hub (optional)
- Amazon ECR (future)

Production always deploys immutable image tags.

---

# 31.12 Staging Deployment

Automatically deploy after successful CI.

Run

- Smoke Tests
- Health Checks
- API Verification

Deployment fails if health checks fail.

---

# 31.13 Production Deployment

Requires

- Successful staging
- Manual approval
- Successful smoke tests

Production deployments are fully automated after approval.

---

# 31.14 Rollback

Rollback is triggered when

- Health checks fail
- Error rate exceeds threshold
- Deployment verification fails

Rollback restores the previous stable version.

---

# 31.15 Environment Configuration

Separate environments

```
Local

Development

Staging

Production
```

Configuration is externalized.

---

# 31.16 Secrets Management

Store secrets in

GitHub Secrets

Examples

- JWT_SECRET
- MONGO_URI
- REDIS_PASSWORD
- KAFKA_PASSWORD
- SUPABASE_KEY

Secrets are never committed to Git.

---

# 31.17 Notifications

Notify team when

- Build fails
- Tests fail
- Deployment succeeds
- Deployment fails

Future

Slack

Microsoft Teams

Email

---

# 31.18 Release Strategy

Every production release

- Tagged
- Documented
- Versioned
- Archived

Release notes generated automatically.

---

# 31.19 Branch Rules

Feature branches

↓

Pull Request

↓

CI

↓

Review

↓

Merge into develop

↓

Release

↓

main

No direct commits to protected branches.

---

# 31.20 Monitoring Deployment

After deployment verify

- Health endpoint
- Error rate
- CPU
- Memory
- Kafka
- Redis
- MongoDB

Deployment is considered successful only after verification.

---

# 31.21 Pipeline Performance

Target times

Build

< 5 min

Unit Tests

< 3 min

Integration Tests

< 10 min

Docker Build

< 5 min

Deployment

< 10 min

---

# 31.22 Failure Handling

Pipeline stops immediately when

- Build fails
- Tests fail
- Security scan fails
- Coverage below threshold
- Docker build fails

No deployment occurs after failure.

---

# 31.23 Engineering Rules

CI/CD must

- Be automated
- Be reproducible
- Run all tests
- Enforce quality gates
- Version every release

Must Not

- Skip tests
- Skip security scans
- Deploy failing builds
- Deploy mutable images

---

# 31.24 Definition of Done

CI/CD is complete when

✓ GitHub Actions configured

✓ Build automated

✓ Tests automated

✓ Security scanning enabled

✓ Docker publishing enabled

✓ Staging deployment automated

✓ Production deployment automated

✓ Rollback documented

✓ Release notes generated

✓ Production-ready

---

# 31.25 Chapter Summary

Duralap's CI/CD pipeline automates the entire software delivery lifecycle, from source code to production deployment. By combining automated builds, quality gates, testing, security scanning, containerization, staged deployments, and rollback capabilities, the platform achieves reliable, repeatable, and enterprise-grade software delivery.


# Chapter 32 - Engineering Principles

---

# Duralap Engineering Bible

## Chapter 32

**Engineering Principles**

Version: 1.0

Status: Approved

---

# 32.1 Purpose

This chapter defines the engineering principles that guide every architectural, technical, and product decision in Duralap.

These principles apply to

- Architecture
- Development
- Code Reviews
- Testing
- Deployment
- Operations

When two solutions are technically correct, engineers should choose the one that aligns best with these principles.

---

# 32.2 Core Philosophy

Duralap is engineered to be

- Reliable
- Secure
- Maintainable
- Observable
- Scalable
- Simple

The goal is not to write the most clever code.

The goal is to build software that survives years of continuous development.

---

# 32.3 Engineering Values

Every engineer should value

- Correctness
- Simplicity
- Readability
- Maintainability
- Reliability
- Performance
- Security
- Collaboration

No single value should dominate at the expense of others.

---

# 32.4 Simplicity First

Choose the simplest solution that satisfies the requirements.

Avoid unnecessary complexity.

Avoid premature abstraction.

Avoid unnecessary frameworks.

Simple systems are easier to understand, test, and maintain.

---

# 32.5 Correctness Before Performance

The order of priorities is

```
Correctness

↓

Security

↓

Reliability

↓

Maintainability

↓

Performance
```

Performance optimizations must never introduce incorrect behavior.

---

# 32.6 Security by Design

Security is designed into the system.

It is not added later.

Every feature must consider

- Authentication
- Authorization
- Validation
- Auditability
- Data Protection

---

# 32.7 Architecture Before Code

Before implementing a feature

Understand

↓

Design

↓

Review

↓

Implement

↓

Test

Never begin implementation without understanding its architectural impact.

---

# 32.8 Modular Design

The system is divided into independent modules.

Each module owns

- Business logic
- Data
- APIs

Modules communicate through well-defined interfaces.

---

# 32.9 Single Responsibility

Every

Class

Function

Module

Service

should have one clear responsibility.

---

# 32.10 Explicit Design

Prefer explicit code over implicit behavior.

Example

Good

```
publishMessageEvent()
```

Avoid

```
process()
```

Code should clearly express intent.

---

# 32.11 Fail Fast

Detect problems as early as possible.

Examples

- Validate input immediately
- Reject invalid states
- Throw meaningful exceptions

Do not silently ignore errors.

---

# 32.12 Automation Over Manual Work

Automate

- Testing
- Formatting
- Static Analysis
- Deployment
- Documentation Generation

Manual work introduces inconsistency.

---

# 32.13 Data Integrity

Protect data integrity.

Never

- Lose messages
- Corrupt user data
- Ignore failed writes

Consistency is more important than convenience.

---

# 32.14 Observability

Every important operation should be observable.

Support

- Logs
- Metrics
- Traces
- Health Checks

Systems that cannot be observed cannot be reliably operated.

---

# 32.15 Documentation

Architecture decisions must be documented.

Documentation evolves together with code.

Documentation is part of the product.

---

# 32.16 Backward Compatibility

Public APIs should remain backward compatible whenever possible.

Breaking changes require

- Justification
- Documentation
- Migration plan

---

# 32.17 Continuous Improvement

Engineering is iterative.

Regularly

- Refactor
- Improve
- Simplify
- Optimize

Technical debt should be managed intentionally.

---

# 32.18 Ownership

Every module has a clear owner.

Owners are responsible for

- Quality
- Documentation
- Testing
- Performance
- Security

Ownership encourages accountability.

---

# 32.19 Decision Making

Engineering decisions should be based on

- Requirements
- Evidence
- Trade-offs
- Long-term maintainability

Avoid decisions driven solely by trends or personal preference.

---

# 32.20 Quality Over Speed

Delivering reliable software is more important than delivering quickly.

A delayed, correct release is preferable to a fast, unstable release.

---

# 32.21 Learning Culture

Engineers are encouraged to

- Learn continuously
- Share knowledge
- Review code constructively
- Improve processes

Knowledge sharing strengthens the team.

---

# 32.22 Engineering Rules

Every engineer must

- Respect architecture
- Write clean code
- Test thoroughly
- Document important decisions
- Review code carefully

Must Not

- Bypass security
- Ignore failing tests
- Introduce unnecessary complexity
- Sacrifice maintainability for short-term gains

---

# 32.23 Definition of Done

An engineering decision is complete when

✓ Requirements understood

✓ Architecture reviewed

✓ Code implemented

✓ Tests passing

✓ Documentation updated

✓ Security reviewed

✓ Performance verified

✓ Monitoring considered

✓ Ready for production

---

# 32.24 Engineering Manifesto

We build software that is

Reliable before impressive.

Simple before clever.

Secure before convenient.

Maintainable before optimized.

Observable before scalable.

Tested before deployed.

Documented before forgotten.

Every line of code should make the system easier—not harder—to evolve.

---

# 32.25 Chapter Summary

The Engineering Principles define the philosophy behind every technical decision in Duralap. They ensure that architecture, implementation, testing, deployment, and operations remain aligned with the long-term vision of building a secure, reliable, maintainable, and scalable messaging platform.

# Chapter 33 - Architecture Decision Records (ADRs)

---

# Duralap Engineering Bible

## Chapter 33

**Architecture Decision Records (ADRs)**

Version: 1.0

Status: Approved

---

# 33.1 Purpose

This chapter defines how architectural decisions are documented.

Architecture Decision Records (ADRs) preserve the reasoning behind significant technical decisions.

Goals

- Preserve engineering knowledge
- Explain architectural choices
- Improve onboarding
- Avoid repeated discussions
- Maintain historical context

Every major architectural decision must have an ADR.

---

# 33.2 What is an ADR?

An ADR is a lightweight document that captures

- Context
- Decision
- Alternatives
- Consequences

An ADR explains "why" rather than "how".

---

# 33.3 When to Create an ADR

Create an ADR when making decisions about

- Architecture
- Database
- Messaging
- Security
- Infrastructure
- Deployment
- Performance
- Technology Selection

Minor implementation details do not require ADRs.

---

# 33.4 ADR Repository

Store ADRs in

```
docs/

└── decisions/

    ADR-0001-Modular-Monolith.md

    ADR-0002-MongoDB.md

    ADR-0003-Redis.md

    ADR-0004-Kafka.md

    ADR-0005-WebSocket.md
```

ADRs are version controlled.

---

# 33.5 ADR Numbering

Sequential numbering

```
ADR-0001

ADR-0002

ADR-0003
```

Numbers are never reused.

---

# 33.6 ADR Status

Possible states

```
Proposed

Accepted

Deprecated

Superseded
```

Only Accepted ADRs define current architecture.

---

# 33.7 ADR Template

Every ADR includes

Title

Status

Date

Context

Decision

Alternatives Considered

Consequences

References

---

# 33.8 Context

Describe

- Current problem
- Requirements
- Constraints
- Existing architecture

Context should explain why the decision is necessary.

---

# 33.9 Decision

Clearly describe

What was chosen

Why it was chosen

The decision should be specific and unambiguous.

---

# 33.10 Alternatives

Document alternatives considered.

Example

MongoDB

vs

PostgreSQL

vs

Cassandra

Explain trade-offs.

---

# 33.11 Consequences

Document

Benefits

Costs

Risks

Future work

Every architectural decision has consequences.

---

# 33.12 References

Link to

- Engineering Bible chapters
- RFCs
- External documentation
- Research
- Benchmarks

---

# 33.13 Updating ADRs

Architecture evolves.

Do not edit historical ADRs.

Instead

Create a new ADR

↓

Reference previous ADR

↓

Mark previous ADR as Superseded

---

# 33.14 Review Process

Every ADR requires review.

Reviewers evaluate

- Technical correctness
- Long-term impact
- Security
- Performance
- Maintainability

Accepted ADRs become part of the official architecture.

---

# 33.15 Example ADR Topics

Examples

ADR-0001

Modular Monolith Architecture

ADR-0002

MongoDB as Primary Database

ADR-0003

Redis for Caching

ADR-0004

Apache Kafka for Events

ADR-0005

Spring WebSocket + STOMP

ADR-0006

Supabase Storage

ADR-0007

JWT Authentication

ADR-0008

Cursor Pagination

ADR-0009

Outbox Pattern

ADR-0010

GitHub Actions CI/CD

---

# 33.16 Engineering Rules

ADRs must

- Explain why
- Be concise
- Be version controlled
- Include alternatives
- Include consequences

Must Not

- Describe implementation details
- Duplicate documentation
- Replace code comments

---

# 33.17 Definition of Done

ADR process is complete when

✓ ADR template created

✓ Repository configured

✓ Review process established

✓ Numbering standardized

✓ Status lifecycle defined

✓ Historical decisions preserved

✓ Production-ready

---

# 33.18 Chapter Summary

Architecture Decision Records preserve the reasoning behind Duralap's most important technical decisions. They improve long-term maintainability, reduce repeated discussions, simplify onboarding, and provide a historical record of how the architecture has evolved over time.


# Chapter 34 - AI Rules

---

# Duralap Engineering Bible

## Chapter 34

**AI Development Rules**

Version: 1.0

Status: Approved

---

# 34.1 Purpose

This chapter defines how Artificial Intelligence tools and AI coding agents may participate in the development of Duralap.

AI is an engineering assistant.

AI is not the architect.

AI is not the final reviewer.

Human engineers remain responsible for all decisions.

---

# 34.2 Goals

AI should

- Increase productivity
- Reduce repetitive work
- Improve documentation
- Generate boilerplate
- Suggest improvements
- Assist debugging

AI must never reduce software quality.

---

# 34.3 Approved AI Tools

Examples

- ChatGPT
- Cursor
- Claude Code
- GitHub Copilot
- Gemini
- Local LLMs

Other tools require engineering approval.

---

# 34.4 AI Responsibilities

AI may assist with

- Code generation
- Refactoring
- Documentation
- Test generation
- API implementation
- Code explanation
- Architecture analysis
- Bug investigation

AI should work within the project's established architecture.

---

# 34.5 AI Limitations

AI must not

- Invent requirements
- Change business logic without approval
- Ignore architecture
- Introduce unnecessary dependencies
- Delete working features without justification
- Expose secrets
- Modify production configuration without review

---

# 34.6 Architecture First

Before generating code

AI must

Read

PROJECT_CONTEXT.md

↓

ARCHITECTURE.md

↓

BACKEND_RULES.md

↓

Relevant Engineering Bible chapters

↓

Existing implementation

Only then may implementation begin.

---

# 34.7 Preserve Existing Behavior

AI should

Refactor safely.

Existing functionality must continue to work unless a documented feature change requires different behavior.

---

# 34.8 Module Boundaries

AI must respect module ownership.

Example

Identity Module

↓

Must not directly modify

Conversation Module

without documented reason.

---

# 34.9 Business Logic

Business rules belong only inside

Service Layer

Use Cases

AI must never place business logic inside

Controllers

Repositories

Configurations

DTOs

---

# 34.10 DTO Rules

AI must

Use DTOs

Never expose entities

Validate input

Return consistent responses

---

# 34.11 Security Rules

AI must never

Bypass authentication

Bypass authorization

Disable validation

Log secrets

Store credentials in code

Skip ownership checks

Security is mandatory.

---

# 34.12 Database Rules

AI must

Review existing indexes

Avoid breaking migrations

Avoid unnecessary schema changes

Prefer additive changes

Never delete production data structures without explicit approval.

---

# 34.13 Redis Rules

AI must

Respect TTL

Respect cache invalidation

Avoid cache pollution

Avoid unbounded keys

---

# 34.14 Kafka Rules

AI must

Maintain event compatibility

Respect ordering

Use versioned events

Avoid breaking consumers

Follow Outbox Pattern.

---

# 34.15 WebSocket Rules

AI must

Respect authentication

Preserve event contracts

Avoid duplicate events

Support reconnection

Avoid breaking clients

---

# 34.16 Testing Rules

AI-generated code must include

Unit Tests

Integration Tests (when applicable)

Regression verification

Generated code without tests is incomplete.

---

# 34.17 Documentation Rules

Whenever AI changes

Architecture

API

Database

Security

Configuration

Documentation must also be updated.

Code and documentation must remain synchronized.

---

# 34.18 Refactoring Rules

AI must

Analyze

↓

Explain problems

↓

Explain solution

↓

Refactor

↓

Test

↓

Document

↓

Verify

Never rewrite large portions of the codebase without approval.

---

# 34.19 Code Quality

Generated code must follow

- Clean Code
- SOLID
- DRY
- KISS
- YAGNI

Generated code should match existing coding standards.

---

# 34.20 Review Process

Every AI-generated change requires human review.

Review includes

- Architecture
- Security
- Performance
- Tests
- Documentation

AI output is never merged automatically.

---

# 34.21 Prompt Rules

Every AI prompt should include

- Project context
- Module name
- Objective
- Constraints
- Relevant architecture rules
- Expected output

Ambiguous prompts should be clarified before implementation.

---

# 34.22 Forbidden Actions

AI must never

- Rewrite the entire project
- Rename modules without approval
- Ignore coding standards
- Introduce experimental libraries
- Disable CI
- Disable tests
- Commit secrets
- Remove monitoring
- Remove logging

---

# 34.23 Engineering Rules

AI must

Respect architecture

Respect module boundaries

Generate tests

Generate documentation

Explain trade-offs

Produce deterministic output

Must Not

Guess requirements

Break compatibility

Ignore errors

Silently change behavior

---

# 34.24 Definition of Done

AI-assisted work is complete when

✓ Requirements understood

✓ Architecture reviewed

✓ Existing code analyzed

✓ Code generated

✓ Tests updated

✓ Documentation updated

✓ Build passes

✓ Review completed

✓ Production-ready

---

# 34.25 AI Manifesto

AI accelerates engineering.

Humans own engineering.

Architecture is never delegated.

Security is never optional.

Documentation is never forgotten.

Testing is never skipped.

Every AI-generated change must make Duralap easier to maintain than before.

---

# 34.26 Chapter Summary

Duralap's AI Rules establish a structured collaboration model between human engineers and AI assistants. AI is used to improve productivity while preserving architectural integrity, security, maintainability, and code quality. Human review remains mandatory for all significant engineering decisions.



# Chapter 35 - Implementation Blueprint

---

# Duralap Engineering Bible

## Chapter 35

**Implementation Blueprint**

Version: 1.0

Status: Approved

---

# 35.1 Purpose

This chapter defines the implementation roadmap for Duralap.

It converts the architecture into an executable engineering plan.

Every implementation should follow this blueprint.

Never implement modules randomly.

Always follow the defined order.

---

# 35.2 Engineering Philosophy

Implementation must be

Incremental

Safe

Testable

Documented

Reviewable

Deployable

Every completed step must leave the project in a working state.

---

# 35.3 Development Workflow

Every feature follows

Requirements

↓

Architecture

↓

Implementation

↓

Testing

↓

Documentation

↓

Review

↓

Merge

↓

Deployment

---

# 35.4 Phase Overview

Phase 1

Project Foundation

Phase 2

Identity

Phase 3

User

Phase 4

Conversation

Phase 5

Messaging

Phase 6

Group

Phase 7

Media

Phase 8

Notification

Phase 9

Search

Phase 10

Performance

Phase 11

Production Hardening

---

# 35.5 Phase 1 — Project Foundation

Objectives

Setup repository

Configure Gradle

Configure Spring Boot

Configure MongoDB

Configure Redis

Configure Kafka

Configure Docker

Configure CI/CD

Configure Security

Deliverables

✓ Project builds

✓ Docker works

✓ Health endpoint

✓ Logging

✓ OpenAPI

✓ Monitoring

---

# 35.6 Phase 2 — Identity Module

Implement

Registration

Login

Refresh Token

Logout

JWT

Password Hashing

Role Management

Authentication Middleware

Deliverables

✓ Authentication complete

✓ Unit Tests

✓ Integration Tests

✓ Documentation

---

# 35.7 Phase 3 — User Module

Implement

User Profile

Avatar

Settings

Presence

Privacy

Status

Blocking

Deliverables

✓ CRUD

✓ Validation

✓ Tests

---

# 35.8 Phase 4 — Conversation Module

Implement

Direct Conversation

Conversation Creation

Conversation Metadata

Members

Archive

Mute

Pin

Deliverables

✓ REST API

✓ Repository

✓ Tests

---

# 35.9 Phase 5 — Messaging Module

Implement

Send Message

Edit Message

Delete Message

Reply

Forward

Reaction

Read Receipt

Delivery Receipt

Pagination

Kafka Events

WebSocket

Deliverables

✓ Real-time Messaging

✓ Event Driven

✓ Tested

---

# 35.10 Phase 6 — Group Module

Implement

Create Group

Members

Roles

Permissions

Invite

Leave

Remove

Group Settings

Deliverables

✓ Group Chat

✓ Permissions

✓ Tests

---

# 35.11 Phase 7 — Media Module

Implement

Upload

Download

Metadata

Preview

Validation

Supabase Storage

Deliverables

✓ Upload API

✓ Storage Integration

✓ Tests

---

# 35.12 Phase 8 — Notification Module

Implement

Push Notification

WebSocket Notification

Unread Count

Mention

Delivery

Kafka Consumer

Deliverables

✓ Notification Service

✓ Tests

---

# 35.13 Phase 9 — Search Module

Implement

User Search

Conversation Search

Message Search

Pagination

Filters

Deliverables

✓ Search API

✓ Indexes

✓ Tests

---

# 35.14 Phase 10 — Performance

Implement

Redis Cache

Indexes

Query Optimization

Pagination

Monitoring

Load Testing

Deliverables

✓ Performance Targets

---

# 35.15 Phase 11 — Production Hardening

Implement

Monitoring

Backups

Rate Limiting

Audit Logs

Metrics

Alerts

Security Review

Deployment

Deliverables

✓ Production Ready

---

# 35.16 Feature Implementation Checklist

Before starting

✓ Requirement understood

✓ Architecture reviewed

✓ API designed

✓ Database reviewed

During implementation

✓ Clean Code

✓ Tests

✓ Documentation

After implementation

✓ Review

✓ Build

✓ Deploy

---

# 35.17 Module Completion Criteria

A module is complete only when

✓ APIs finished

✓ Validation implemented

✓ Security implemented

✓ Tests passing

✓ Documentation updated

✓ OpenAPI updated

✓ Monitoring added

✓ Performance reviewed

---

# 35.18 Migration Strategy

For existing code

Analyze

↓

Compare

↓

Refactor

↓

Test

↓

Document

↓

Merge

Never rewrite the entire project.

---

# 35.19 AI Implementation Rules

AI must

Analyze existing code

↓

Read Engineering Bible

↓

Compare architecture

↓

Implement incrementally

↓

Generate tests

↓

Update documentation

↓

Verify build

AI must never skip analysis.

---

# 35.20 Engineering Gates

Before merging

Build

↓

ktlint

↓

Detekt

↓

Unit Tests

↓

Integration Tests

↓

Security Scan

↓

Coverage

↓

Review

↓

Merge

---

# 35.21 Release Gates

Before production

✓ Architecture verified

✓ Documentation complete

✓ Tests passing

✓ Performance verified

✓ Security reviewed

✓ Monitoring active

✓ Rollback available

---

# 35.22 Progress Tracking

Track

Module

Status

Coverage

Performance

Documentation

Open Issues

Technical Debt

Use GitHub Projects or similar tooling.

---

# 35.23 Definition of Done

The implementation blueprint is complete when

✓ Every module implemented

✓ Architecture respected

✓ Tests passing

✓ Documentation synchronized

✓ CI/CD operational

✓ Monitoring enabled

✓ Production deployment successful

---

# 35.24 Chapter Summary

The Implementation Blueprint transforms Duralap's architecture into a structured execution plan. By implementing modules incrementally, enforcing quality gates, and validating each phase before proceeding, the project minimizes risk while ensuring long-term maintainability, scalability, and production readiness.


# Chapter 36 - Future Roadmap

---

# Duralap Engineering Bible

## Chapter 36

**Future Roadmap**

Version: 1.0

Status: Approved

---

# 36.1 Purpose

This chapter defines the long-term evolution of Duralap.

It provides a strategic roadmap for future features,
architecture evolution,
platform expansion,
and engineering maturity.

The roadmap is aspirational.

Implementation priorities may change based on business needs.

---

# 36.2 Vision

Duralap aims to become a modern, secure, scalable communication platform that supports

- Personal Messaging
- Group Collaboration
- Communities
- Business Communication
- Cross-platform Experiences
- AI-powered Productivity

The architecture should support continuous growth without major redesign.

---

# 36.3 Roadmap Philosophy

Every new feature should

- Solve a real user problem
- Respect existing architecture
- Be backward compatible where possible
- Maintain performance targets
- Preserve security
- Be fully documented

Features should not be added solely because competitors have them.

---

# 36.4 Version Roadmap

Version 1.x

Core Messaging Platform

Version 2.x

Productivity & Collaboration

Version 3.x

Business Platform

Version 4.x

AI Platform

Version 5.x

Global Distributed Platform

---

# 36.5 Version 1.x

Objectives

- Authentication
- User Profiles
- Conversations
- Messaging
- Groups
- Media
- Notifications
- Search
- Monitoring
- Production Deployment

Goal

Stable production release.

---

# 36.6 Version 2.x

Collaboration Features

- Voice Messages
- Message Pinning
- Polls
- Scheduled Messages
- Message Drafts
- Rich Link Preview
- Shared Files
- Shared Notes
- Calendar Integration
- Workspace Support

Goal

Improve collaboration.

---

# 36.7 Version 3.x

Business Features

- Business Accounts
- Organization Management
- Team Administration
- Audit Reports
- Analytics Dashboard
- Customer Support Inbox
- Role Templates
- Enterprise SSO
- API Keys
- Webhooks

Goal

Support enterprise customers.

---

# 36.8 Version 4.x

AI Features

- AI Chat Assistant
- Conversation Summaries
- Smart Search
- AI Translation
- Spam Detection
- Smart Notifications
- AI Moderation
- Auto Replies
- Knowledge Assistant
- Meeting Notes

Goal

Increase productivity through AI.

---

# 36.9 Version 5.x

Global Scale

- Multi-region Deployment
- Global CDN
- Regional Storage
- Multi-cluster Kafka
- Distributed Redis
- Geo-routing
- Active-Active Deployment
- Disaster Recovery Automation

Goal

Worldwide availability.

---

# 36.10 Mobile Roadmap

Android

Native Kotlin

iOS

Native Swift

Desktop

Compose Multiplatform (Future)

Web

React / Next.js

Future

Wear OS

Apple Watch

Android Auto

---

# 36.11 Backend Evolution

Current

Modular Monolith

Future

Modular Monolith

↓

Service Extraction

↓

Selective Microservices

↓

Multi-region Platform

Microservices are adopted only when justified by scale.

---

# 36.12 Database Evolution

Current

MongoDB Replica Set

Future

MongoDB Sharding

↓

Read Replicas

↓

Global Distribution

---

# 36.13 Redis Evolution

Current

Single Redis

Future

Redis Sentinel

↓

Redis Cluster

↓

Multi-region Cache

---

# 36.14 Kafka Evolution

Current

Single Cluster

Future

Multiple Brokers

↓

Multi-cluster

↓

Cross-region Replication

---

# 36.15 Security Roadmap

Future improvements

- Passkeys
- Hardware Security Keys
- Device Trust
- Risk-based Authentication
- Adaptive MFA
- Security Center
- Compliance Dashboard

---

# 36.16 Performance Roadmap

Future optimization

- Edge Caching
- HTTP/3
- gRPC (Internal)
- Binary WebSocket Payloads
- Compression Improvements
- Query Optimization
- Adaptive Cache

---

# 36.17 Observability Roadmap

Future

- Distributed Tracing
- Service Maps
- Real User Monitoring
- Synthetic Monitoring
- AI-assisted Incident Detection
- Capacity Forecasting

---

# 36.18 Developer Experience

Improve

- CLI Tools
- Local Development
- Automated Documentation
- Better AI Integration
- Faster CI/CD
- Architecture Validation

Developer productivity is a long-term investment.

---

# 36.19 Community Roadmap

Future

- Public API
- SDKs
- Developer Portal
- Plugin System
- Open Documentation
- Community Extensions

---

# 36.20 Success Metrics

Measure

- Active Users
- Daily Messages
- API Latency
- Deployment Frequency
- Uptime
- Crash Rate
- Test Coverage
- Customer Satisfaction

Engineering decisions should be guided by measurable outcomes.

---

# 36.21 Engineering Rules

Future work must

- Respect architecture
- Preserve compatibility
- Be measurable
- Be documented
- Be reviewed

Must Not

- Introduce unnecessary complexity
- Ignore technical debt
- Bypass security
- Sacrifice maintainability

---

# 36.22 Living Roadmap

The roadmap is reviewed

- Quarterly
- Before major releases
- After architecture changes

Completed items are archived with release notes.

---

# 36.23 Definition of Done

The roadmap is complete when

✓ Long-term vision documented

✓ Product milestones defined

✓ Architecture evolution planned

✓ Technology evolution planned

✓ Business growth planned

✓ AI roadmap documented

✓ Infrastructure roadmap documented

✓ Regular review process established

---

# 36.24 Chapter Summary

The Future Roadmap provides Duralap with a structured vision beyond the initial release. It outlines the evolution of the platform across product features, infrastructure, AI capabilities, security, performance, and global scalability while ensuring that every future enhancement remains aligned with the architectural principles established throughout the Engineering Bible.




2026
│
├── Modular Monolith
├── MongoDB
├── Redis
├── Kafka
└── WebSocket

2027
│
├── Voice Messages
├── Polls
├── AI Search
└── Business Accounts

2028
│
├── Kubernetes
├── Redis Cluster
├── MongoDB Sharding
└── Enterprise SSO

2029
│
├── Multi-Region
├── Global CDN
├── AI Assistant
└── Public APIs

2030+
│
├── Federated Messaging
├── Plugin Marketplace
├── AI Agents
└── Global Platform