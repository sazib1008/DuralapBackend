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
    IDLE,
    INITIATED,    // Call initiated by caller
    CALLING,      // Outgoing call attempt in progress
    RINGING,      // Callee is ringing
    ACCEPTED,     // Callee accepted call
    CONNECTING,   // WebRTC peer connection establishing
    CONNECTED,    // Media stream established
    ACTIVE,       // Active call session (synonym to CONNECTED)
    ENDED,        // Call ended normally
    REJECTED,     // Call rejected by callee
    BUSY,         // Callee is busy in another call
    CANCELLED,    // Caller cancelled before answer
    TIMEOUT,      // Ringing timed out without answer
    MISSED,       // Call missed by callee
    FAILED        // Call failed due to network / media error
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

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}
