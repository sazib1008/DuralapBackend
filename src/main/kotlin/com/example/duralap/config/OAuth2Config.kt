package com.example.duralap.config

import com.example.duralap.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 * Configuration class prepared for future OAuth2.0 integration
 * Currently uses custom JWT authentication but ready to integrate OAuth2 providers
 */
@Configuration
class OAuth2Config {
    
    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider
    
    /**
     * Custom logout handler for OAuth2 sessions
     */
    @Bean
    fun oauth2LogoutSuccessHandler(): LogoutSuccessHandler {
        return LogoutSuccessHandler { _, response, _ ->
            // Clear JWT tokens and redirect as needed
            response?.sendRedirect("/login?logout")
        }
    }
}