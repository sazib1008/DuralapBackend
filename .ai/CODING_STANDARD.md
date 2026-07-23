# Kotlin Coding Standard

## Language

Kotlin

Java 21

Spring Boot 3

---

## Naming

Class

PascalCase

Method

camelCase

Variable

camelCase

Constant

UPPER_SNAKE_CASE

Package

lowercase

---

## Constructor Injection

Always

Never use field injection.

---

## Null Safety

Prefer non-null.

Avoid !!

Use nullable only when required.

---

## Data Classes

Use immutable data classes.

Prefer val.

Avoid var.

---

## Functions

Small.

Focused.

One responsibility.

Maximum recommended

30–40 lines.

---

## Classes

Small.

Single responsibility.

Avoid God classes.

---

## Comments

Explain WHY.

Never explain WHAT.

Good

// Retry because Kafka delivery is asynchronous.

Bad

// Increment i

---

## Exceptions

Never throw generic Exception.

Create domain-specific exceptions.

---

## Coroutines

Use suspend functions where appropriate.

Avoid blocking operations.

---

## Transactions

Keep transactions small.

Never call remote services inside transactions.

---

## Mapping

Use dedicated Mapper classes.

Never map inside controllers.

---

## API

Always

Request DTO

↓

UseCase

↓

Response DTO

Never expose Entity.

---

## Formatting

4 spaces.

Meaningful names.

No abbreviations.

No unused imports.

No wildcard imports.

---

## Code Review Checklist

Before every commit verify

✅ Architecture respected

✅ Tests passed

✅ Validation exists

✅ Logging exists

✅ Authorization exists

✅ Exception handling exists

✅ OpenAPI updated

✅ No duplicated code

✅ No hardcoded secrets

✅ Clean code

Only production-quality code is acceptable.