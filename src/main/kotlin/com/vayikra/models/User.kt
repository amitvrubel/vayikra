package com.vayikra.models

import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val city: String,
    val country: String,
    @Transient internal val passwordHash: String? = null,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val city: String,
    val country: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
)
