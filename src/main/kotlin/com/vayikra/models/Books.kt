package com.vayikra.models

import java.util.UUID
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class BookStatus { AVAILABLE, REQUESTED, IN_TRANSIT, WITH_READER }

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val imageUrl: String? = null,
    val status: BookStatus = BookStatus.AVAILABLE,
    val notes: String? = null,
    val createdAt: Instant,
)

@Serializable
data class BookDto(
    val id: String,
    val ownerId: String,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val imageUrl: String? = null,
    val status: BookStatus = BookStatus.AVAILABLE,
    val notes: String? = null,
    val createdAt: String,
)

@Serializable
data class CreateBookRequest(
    val title: String,
    val author: String,
    val isbn: String? = null,
    val imageUrl: String? = null,
    val notes: String? = null,
)

fun Book.toDto() =
    BookDto(
        id = id,
        ownerId = ownerId,
        title = title,
        author = author,
        isbn = isbn,
        imageUrl = imageUrl,
        status = status,
        notes = notes,
        createdAt = createdAt.toString(),
    )
