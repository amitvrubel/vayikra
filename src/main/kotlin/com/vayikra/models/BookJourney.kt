package com.vayikra.com.vayikra.models

import java.util.UUID
import kotlin.time.Instant
import kotlinx.serialization.Serializable

data class BookJourneyEntry(
    val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val fromUserId: String,
    val toUserId: String,
    val fromCity: String,
    val toCity: String,
    val transferredAt: Instant,
)

@Serializable
data class BookJourneyEntryDto(
    val id: String,
    val bookId: String,
    val fromUserId: String,
    val toUserId: String,
    val fromCity: String,
    val toCity: String,
    val transferredAt: Instant,
)

fun BookJourneyEntry.toDto(): BookJourneyEntryDto =
    BookJourneyEntryDto(
        id = id,
        bookId = bookId,
        fromUserId = fromUserId,
        toUserId = toUserId,
        fromCity = fromCity,
        toCity = toCity,
        transferredAt = transferredAt,
    )
