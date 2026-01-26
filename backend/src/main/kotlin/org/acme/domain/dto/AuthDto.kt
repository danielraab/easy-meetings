package org.acme.domain.dto

import java.util.*

data class UserDto(
    val id: UUID?,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val isActive: Boolean
)

data class LoginRequest(
    val email: String
)

data class MagicLinkVerifyRequest(
    val token: String
)

data class AuthResponse(
    val user: UserDto,
    val message: String = "Authentication successful"
)
