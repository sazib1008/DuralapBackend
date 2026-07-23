# Database Rules

Project:
Duralap

Database:
MongoDB

Role:
Primary Source of Truth

---

# Principles

MongoDB is the only permanent database.

Redis is cache only.

Kafka is event streaming only.

Supabase stores media files only.

Never duplicate business truth.

---

# Collections

users

user_profiles

user_privacy

blocked_users

user_devices

refresh_tokens

email_verifications

password_reset_tokens

conversations

conversation_members

groups

group_members

group_settings

messages

message_receipts

message_reactions

media_files

notifications

audit_logs

outbox_events

---

# ID Strategy

Use MongoDB ObjectId.

Never expose database implementation details unnecessarily.

Treat ObjectId as an opaque identifier.

---

# Schema Rules

Every document should include

createdAt

updatedAt

createdBy (if applicable)

updatedBy (if applicable)

version (future optimistic locking)

---

# Index Rules

Every query used frequently must have an index.

No collection scan in production.

Review indexes whenever adding new queries.

---

# Repository Rules

One repository per aggregate.

Repositories only perform persistence.

No business logic inside repositories.

---

# Soft Delete

Prefer soft delete for business entities.

Audit log must be created.

---

# Transactions

Use MongoDB transactions only when required.

Keep transactions short.

Never call external services inside a transaction.

---

# Migration

Never modify existing documents manually.

Create migration scripts.

Every schema change must be backward compatible.

---

# Performance

Avoid N+1 queries.

Project only required fields.

Use pagination.

Cursor pagination preferred.

---

# AI Rules

Never generate repositories with business logic.

Always create indexes for searchable fields.

Always consider scalability.