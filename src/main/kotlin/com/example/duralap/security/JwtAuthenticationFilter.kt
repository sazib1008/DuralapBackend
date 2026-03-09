package com.example.duralap.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // Get JWT token from HTTP request
            val token = getJwtTokenFromRequest(request)

            if (token != null && StringUtils.hasText(token)) {
                // Validate token and check if it's an access token
                if (isValidAccessToken(token)) {
                    val username = getUsernameSafely(token)
                    
                    username?.let { 
                        // Load user associated with the token
                        val userDetails = userDetailsService.loadUserByUsername(it)

                        // Set authenticated user in Spring Security context
                        val authorities = userDetails.authorities
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails.username,
                            userDetails.password,
                            authorities
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun isValidAccessToken(token: String): Boolean {
        return try {
            jwtTokenProvider.validateToken(token) && jwtTokenProvider.isAccessToken(token)
        } catch (ex: Exception) {
            logger.debug("Token validation failed: ${ex.message}")
            false
        }
    }

    private fun getUsernameSafely(token: String): String? {
        return try {
            jwtTokenProvider.getUsernameFromToken(token)
        } catch (ex: Exception) {
            logger.debug("Failed to extract username from token: ${ex.message}")
            null
        }
    }

    private fun getJwtTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}