package com.example.duralap.auth.application.service

import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username.lowercase())
            .orElseThrow { UsernameNotFoundException("User not found with username: $username") }

        val userPassword = if (user.isOAuth2User) {
            ""
        } else {
            user.password ?: throw IllegalStateException("Regular user must have a password")
        }

        return User(
            user.username,
            userPassword,
            true,
            true,
            true,
            true,
            mapRolesToAuthorities(user.roles)
        )
    }

    private fun mapRolesToAuthorities(roles: Set<com.example.duralap.database.model.Role>): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }
    }
}
