package com.vayikra.com.vayikra.services

import com.vayikra.com.vayikra.models.BookJourneyEntry
import com.vayikra.db.BookJourney
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class BookJourneyService {
    fun getJourneyForBook(bookId: String): List<BookJourneyEntry> =
        transaction {
            BookJourney
                .selectAll()
                .where { BookJourney.bookId eq bookId }
                .orderBy(BookJourney.transferredAt)
                .map(::rowToEntry)
        }

    private fun rowToEntry(row: ResultRow) =
        BookJourneyEntry(
            id = row[BookJourney.bookId],
            bookId = row[BookJourney.bookId],
            fromUserId = row[BookJourney.fromUserId],
            toUserId = row[BookJourney.toUserId],
            fromCity = row[BookJourney.fromCity],
            toCity = row[BookJourney.toCity],
            transferredAt = row[BookJourney.transferredAt],
        )
}
