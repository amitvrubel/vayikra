package com.vayikra.services

import com.vayikra.com.vayikra.models.Book
import com.vayikra.com.vayikra.models.BookStatus
import com.vayikra.db.Books
import java.util.UUID
import kotlin.time.Clock
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class BookService {
    fun getBooksForUser(ownerId: String): List<Book> =
        transaction {
            Books
                .selectAll()
                .where { Books.ownerId eq ownerId }
                .map { row ->
                    Book(
                        id = row[Books.id],
                        title = row[Books.title],
                        author = row[Books.author],
                        ownerId = row[Books.ownerId],
                        isbn = row[Books.isbn],
                        imageUrl = row[Books.imageUrl],
                        notes = row[Books.notes],
                        status = BookStatus.valueOf(row[Books.status]),
                    )
                }
        }

    fun getAllAvailable(): List<Book> =
        transaction {
            Books
                .selectAll()
                .where { Books.status eq BookStatus.AVAILABLE.name }
                .map { row ->
                    Book(
                        id = row[Books.id],
                        ownerId = row[Books.ownerId],
                        title = row[Books.title],
                        author = row[Books.author],
                        isbn = row[Books.isbn],
                        imageUrl = row[Books.imageUrl],
                        status = BookStatus.valueOf(row[Books.status]),
                        notes = row[Books.notes],
                    )
                }
        }

    fun createBook(
        ownerId: String,
        title: String,
        author: String,
        isbn: String?,
        imageUrl: String?,
        notes: String?,
    ): Book {
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()

        transaction {
            Books.insert {
                it[Books.id] = id
                it[Books.ownerId] = ownerId
                it[Books.title] = title
                it[Books.author] = author
                it[Books.isbn] = isbn
                it[Books.imageUrl] = imageUrl
                it[Books.status] = BookStatus.AVAILABLE.name
                it[Books.notes] = notes
                it[Books.createdAt] = now
            }
        }

        return Book(
            id = id,
            ownerId = ownerId,
            title = title,
            author = author,
            isbn = isbn,
            imageUrl = imageUrl,
            notes = notes,
        )
    }
}
