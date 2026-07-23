package com.example.duralap.exception

import org.springframework.http.HttpStatus
import java.time.Instant

data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
