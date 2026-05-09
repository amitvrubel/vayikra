package com.vayikra.models

import java.util.UUID
import kotlin.time.Instant
import kotlinx.serialization.Serializable

data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val city: String,
    val country: String,
    val passwordHash: String,
    val createdAt: Instant,
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val city: String,
    val country: String,
    val createdAt: String,
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
    val user: UserDto,
)

fun User.toDto() =
    UserDto(
        id = id,
        email = email,
        name = name,
        city = city,
        country = country,
        createdAt = createdAt.toString(),
    )
