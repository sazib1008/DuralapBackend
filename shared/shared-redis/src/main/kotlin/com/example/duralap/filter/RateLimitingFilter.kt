package com.example.duralap.filter

import com.example.duralap.config.RateLimitingService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Rate limiting filter that applies rate limits to API endpoints.
 * Uses Redis-based sliding window algorithm for accurate rate limiting.
 */
@Component
class RateLimitingFilter(
    private val rateLimitingService: RateLimitingService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestPath = request.requestURI
        
        // Skip rate limiting for certain endpoints
        if (shouldSkipRateLimiting(requestPath)) {
            filterChain.doFilter(request, response)
            return
        }

        // Get user identifier (from security context or IP address)
        val userId = getUserIdentity(request)
        
        // Apply rate limiting based on endpoint
        var retryAfterSeconds = 60L
        val isAllowed = when {
            requestPath.contains("/api/messages") && request.method == "POST" -> {
                retryAfterSeconds = 60L
                rateLimitingService.canSendMessage(userId)
            }
            requestPath.contains("/api/conversations/start-with") && request.method == "POST" -> {
                retryAfterSeconds = 3600L
                rateLimitingService.canCreateConversationRequest(userId)
            }
            requestPath.contains("/api/users/search") || requestPath.contains("/api/users/check") -> {
                retryAfterSeconds = 60L
                rateLimitingService.canSearchUsers(userId)
            }
            requestPath.contains("/api/auth/login") && request.method == "POST" -> {
                retryAfterSeconds = 900L
                rateLimitingService.canAttemptLogin(getClientIp(request))
            }
            else -> true
        }

        if (!isAllowed) {
            response.status = 429 // HTTP 429 Too Many Requests
            response.addHeader("Retry-After", retryAfterSeconds.toString())
            response.contentType = "application/json"
            response.writer.write("""{
                "error": "Rate limit exceeded",
                "message": "Too many requests. Please try again later.",
                "statusCode": 429
            }""")
            return
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Get user identity for rate limiting
     */
    private fun getUserIdentity(request: HttpServletRequest): String {
        return try {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication != null && authentication.isAuthenticated) {
                "user:${authentication.name}"
            } else {
                "ip:${getClientIp(request)}"
            }
        } catch (e: Exception) {
            "ip:${getClientIp(request)}"
        }
    }

    /**
     * Get client IP address
     */
    private fun getClientIp(request: HttpServletRequest): String {
        val xfHeader = request.getHeader("X-Forwarded-For")
        return when {
            xfHeader.isNullOrBlank() -> request.remoteAddr
            else -> xfHeader.split(",")[0].trim()
        }
    }

    /**
     * Determine if rate limiting should be skipped for this endpoint
     */
    private fun shouldSkipRateLimiting(requestPath: String): Boolean {
        val skipPaths = listOf(
            "/actuator",
            "/websocket",
            "/api/auth/register",  // Registration has its own rate limiting
            "/swagger",
            "/v3/api-docs"
        )
        
        return skipPaths.any { requestPath.startsWith(it) }
    }
}
