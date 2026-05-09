package com.vayikra.models

import java.util.UUID
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class BookRequestStatus { PENDING, ACCEPTED, REJECTED, CANCELLED, SENT, DELIVERED }

data class BookRequest(
    val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val requesterId: String,
    val ownerId: String,
    val status: BookRequestStatus = BookRequestStatus.PENDING,
    val message: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
data class BookRequestDto(
    val id: String,
    val bookId: String,
    val requesterId: String,
    val ownerId: String,
    val status: BookRequestStatus,
    val message: String? = null,
    val createdAt: String,
)

@Serializable
data class CreateBookRequestDto(
    val message: String? = null,
)

@Serializable
data class UpdateBookRequestStatusDto(
    val status: BookRequestStatus,
)

fun BookRequest.toDto() =
    BookRequestDto(
        id = id,
        bookId = bookId,
        requesterId = requesterId,
        ownerId = ownerId,
        status = status,
        message = message,
        createdAt = createdAt.toString(),
    )
