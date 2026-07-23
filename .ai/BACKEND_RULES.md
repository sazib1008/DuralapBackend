# Backend Engineering Rules

## General

Production-ready code only.

Readable code.

Self-documenting code.

No duplicated logic.

No magic numbers.

No hardcoded strings.

---

## Controllers

Controllers only:

Validate request

Call UseCase

Return Response

Nothing else.

---

## Services

Business logic belongs here.

Keep methods small.

Single responsibility.

---

## Repository

Only persistence.

No business logic.

---

## DTO

Never expose entities.

Always use Request DTO.

Always use Response DTO.

---

## Validation

Always use Bean Validation.

Business validation inside UseCases.

Never trust client input.

---

## Security

Always check authorization.

Always verify ownership.

Always validate JWT.

Always validate permissions.

---

## Logging

Structured logging.

Never log:

Password

OTP

JWT

Secrets

Refresh Token

---

## Error Handling

Global exception handler.

Typed exceptions.

Standard error response.

---

## Kafka

Every event

Must contain

Event ID

Timestamp

Correlation ID

Version

Producer

---

## Redis

Never store permanent data.

Use TTL.

Avoid large values.

---

## MongoDB

Use indexes.

Use ObjectId.

Use optimistic updates where appropriate.

---

## Media

Files

↓

Supabase

Metadata

↓

MongoDB

---

## Testing

Every feature must have

Unit Tests

Integration Tests

Repository Tests

Controller Tests

Never merge untested code.