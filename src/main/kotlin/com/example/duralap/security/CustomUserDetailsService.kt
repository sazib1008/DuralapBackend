package com.example.duralap.security

import com.example.duralap.database.repository.UserRepository
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
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found with username or email: $username") }

        // Convert our User entity to Spring Security UserDetails
        return User(
            user.username,
            user.password,
            mapRolesToAuthorities(user.roles)
        )
    }

    private fun mapRolesToAuthorities(roles: Set<com.example.duralap.database.model.Role>): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }
    }
}