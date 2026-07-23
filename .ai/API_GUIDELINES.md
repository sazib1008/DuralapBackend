# API Guidelines

Base URL

/api/v1/

---

# API Style

RESTful

Plural resources

Versioned

Stateless

---

# Response Format

{
  success,
  data,
  meta,
  error,
  timestamp
}

---

# HTTP Methods

GET

POST

PATCH

DELETE

---

# Authentication

JWT required except public endpoints.

---

# Validation

Bean Validation

Business Validation

Ownership Validation

Authorization Validation

---

# Pagination

Cursor-based

Never use offset pagination for messages.

---

# Error Codes

Never return plain text.

Always return

Code

Message

Details (optional)

---

# OpenAPI

Every endpoint must include

Summary

Description

Parameters

Request Body

Responses

Examples

---

# DTO Rules

Never expose entities.

Always use Request DTO

Response DTO

---

# Versioning

All APIs start with

/api/v1/

Future versions

/api/v2/

---

# AI Rules

Never create inconsistent endpoints.

Always follow REST conventions.

Always document endpoints.