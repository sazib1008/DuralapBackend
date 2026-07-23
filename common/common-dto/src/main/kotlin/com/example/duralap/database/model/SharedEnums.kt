package com.example.duralap.database.model

enum class UserStatus {
    ONLINE,
    OFFLINE,
    AWAY,
    BUSY
}

enum class Role {
    USER,
    ADMIN,
    MODERATOR
}

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallStatus {
    INITIATED,    // Call initiated but not answered
    RINGING,      // Call is ringing
    ACTIVE,       // Call is active
    ENDED,        // Call ended normally
    REJECTED,     // Call rejected by callee
    MISSED,       // Call missed by callee
    FAILED,       // Call failed due to technical issues
    BUSY          // Callee is busy in another call
}

enum class ConversationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    BLOCKED
}

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    LOCATION,
    CONTACT
}
