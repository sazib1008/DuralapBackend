package com.example.duralap.database.model

/**
 * Represents the status of a conversation between users.
 * PENDING: Initial state when a user sends a message to someone for the first time
 * ACCEPTED: The recipient has accepted the conversation request
 * REJECTED: The recipient has rejected the conversation request
 * BLOCKED: The conversation is blocked by one of the participants
 */
enum class ConversationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    BLOCKED
}
