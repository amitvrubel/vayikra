package com.vayikra.com.vayikra.models

import java.util.UUID
import kotlinx.serialization.Serializable

enum class BookStatus { AVAILABLE, REQUESTED, IN_TRANSIT, WITH_READER }

@Serializable
data class Book(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val imageUrl: String? = null,
    val status: BookStatus = BookStatus.AVAILABLE,
    val notes: String? = null,
)

@Serializable
data class CreateBookRequest(
    val title: String,
    val author: String,
    val isbn: String? = null,
    val imageUrl: String? = null,
    val notes: String? = null,
)
