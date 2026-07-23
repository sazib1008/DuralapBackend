# Duralap Backend Architecture

## Architecture Style

Feature-Based Modular Monolith

Future migration:

Microservices

---

## Modules

Identity

User

Conversation

Message

Group

Media

Notification

Search

Infrastructure

---

## Communication

Allowed

Controller

↓

UseCase

↓

Repository

Forbidden

Controller

↓

Repository

Forbidden

Module A Repository

↓

Module B Repository

Communication between modules must happen through

- Facades
- Domain Events

---

## Database

MongoDB

Single Source of Truth

Redis

Cache only

Kafka

Events only

Supabase

Media only

---

## Message Flow

Client

↓

REST API

↓

Validation

↓

Authorization

↓

MongoDB

↓

Outbox

↓

Kafka

↓

Notification

↓

WebSocket

---

## Authentication

JWT

Refresh Token

Google OAuth2

Email OTP

---

## Realtime

Spring WebSocket

STOMP

Kafka

Redis Presence

---

## Principles

Clean Architecture

SOLID

DRY

KISS

Repository Pattern

UseCase Pattern

DTO Pattern

Outbox Pattern

Event Driven Architecture

API First

---

## Folder Rule

Each module contains

controller

dto

mapper

entity

repository

usecase

service

event

config

exception

---

Never violate architecture.