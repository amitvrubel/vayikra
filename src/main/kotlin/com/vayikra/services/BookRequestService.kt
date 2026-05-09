package com.vayikra.services

import com.vayikra.db.BookRequests
import com.vayikra.db.Books
import com.vayikra.models.BookRequest
import com.vayikra.models.BookRequestStatus
import java.util.UUID
import kotlin.time.Clock
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class BookRequestService {
    fun createRequest(
        bookId: String,
        requesterId: String,
        ownerId: String,
        message: String?,
    ): BookRequest {
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()

        transaction {
            BookRequests.insert {
                it[BookRequests.id] = id
                it[BookRequests.bookId] = bookId
                it[BookRequests.requesterId] = requesterId
                it[BookRequests.ownerId] = ownerId
                it[BookRequests.status] = BookRequestStatus.PENDING.name
                it[BookRequests.message] = message
                it[BookRequests.createdAt] = now
                it[BookRequests.updatedAt] = now
            }
            Books.update({ Books.id eq bookId }) {
                it[Books.status] = BookRequestStatus.PENDING.name
            }
        }
        return BookRequest(
            id = id,
            bookId = bookId,
            requesterId = requesterId,
            ownerId = ownerId,
            message = message,
            createdAt = now,
            updatedAt = now,
        )
    }

    fun updateStatus(
        requestId: String,
        newStatus: BookRequestStatus,
        updaterId: String,
    ): BookRequest? {
        val now = Clock.System.now()
        return transaction {
            val request =
                BookRequests
                    .selectAll()
                    .where { BookRequests.id eq requestId }
                    .map(::rowToBookRequest)
                    .singleOrNull() ?: return@transaction null

            val isOwner = request.ownerId == updaterId
            val isRequester = request.requesterId == updaterId

            val allowed =
                when (newStatus) {
                    BookRequestStatus.ACCEPTED,
                    BookRequestStatus.REJECTED,
                    BookRequestStatus.SENT,
                    -> isOwner
                    BookRequestStatus.CANCELLED -> isOwner || isRequester
                    BookRequestStatus.DELIVERED -> isRequester
                    BookRequestStatus.PENDING -> false
                }

            if (!allowed) {
                return@transaction null
            }

            BookRequests.update({ BookRequests.id eq requestId }) {
                it[BookRequests.status] = newStatus.name
                it[BookRequests.updatedAt] = now
            }

            val bookStatus =
                when (newStatus) {
                    BookRequestStatus.ACCEPTED -> "REQUESTED"
                    BookRequestStatus.SENT -> "IN_TRANSIT"
                    BookRequestStatus.DELIVERED -> "WITH_READER"
                    BookRequestStatus.REJECTED, BookRequestStatus.CANCELLED -> "AVAILABLE"
                    BookRequestStatus.PENDING -> "REQUESTED"
                }

            Books.update({ Books.id eq request.bookId }) {
                it[Books.status] = bookStatus
            }

            request.copy(status = newStatus, updatedAt = now)
        }
    }

    fun getRequestsForOwner(ownerId: String): List<BookRequest> =
        transaction {
            BookRequests
                .selectAll()
                .where { BookRequests.ownerId eq ownerId }
                .map(::rowToBookRequest)
        }

    fun getRequestsForUser(userId: String): List<BookRequest> =
        transaction {
            BookRequests
                .selectAll()
                .where { BookRequests.requesterId eq userId }
                .map(::rowToBookRequest)
        }

    private fun rowToBookRequest(row: ResultRow) =
        BookRequest(
            id = row[BookRequests.id],
            bookId = row[BookRequests.bookId],
            requesterId = row[BookRequests.requesterId],
            ownerId = row[BookRequests.ownerId],
            status = BookRequestStatus.valueOf(row[BookRequests.status]),
            message = row[BookRequests.message],
            createdAt = row[BookRequests.createdAt],
            updatedAt = row[BookRequests.updatedAt],
        )
}
