package com.example.duralap.auth.service

import com.example.duralap.auth.application.service.AuthService
import com.example.duralap.auth.application.service.RefreshTokenService
import com.example.duralap.database.dto.UserCreateRequest
import com.example.duralap.database.model.Role
import com.example.duralap.security.JwtTokenProvider
import com.example.duralap.user.domain.model.User
import com.example.duralap.user.domain.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var refreshTokenService: RefreshTokenService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    fun `should register user successfully when details are valid`() {
        val request = UserCreateRequest(
            username = "testuser",
            email = "test@example.com",
            password = "securePassword",
            fullName = "Test User",
            bio = "This is a bio",
            phoneNumber = "123456789"
        )
        `when`(userRepository.existsByUsername("testuser")).thenReturn(false)
        `when`(userRepository.existsByEmail("test@example.com")).thenReturn(false)
        `when`(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword")
        `when`(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] as User }

        val response = authService.register(request)

        assertThat(response.username).isEqualTo("testuser")
        assertThat(response.email).isEqualTo("test@example.com")
        assertThat(response.roles).containsExactly(Role.USER)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `should throw exception during registration when username already exists`() {
        val request = UserCreateRequest(
            username = "testuser",
            email = "test@example.com",
            password = "securePassword"
        )
        `when`(userRepository.existsByUsername("testuser")).thenReturn(true)

        assertThatThrownBy { authService.register(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Username already exists")
    }
}
