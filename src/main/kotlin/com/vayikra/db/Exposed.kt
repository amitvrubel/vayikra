package com.vayikra.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object Users : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255)
    val passwordHash = varchar("password_hash", 255)
    val city = varchar("city", 255)
    val country = varchar("country", 100)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Books : Table("books") {
    val id = varchar("id", 36)
    val ownerId = varchar("owner_id", 36).references(Users.id)
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val isbn = varchar("isbn", 13).nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val status = varchar("status", 50)
    val notes = text("notes").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object BookJourney : Table("book_journey") {
    val id = varchar("id", 36)
    val bookId = varchar("book_id", 36).references(Books.id)
    val fromUserId = varchar("from_user_id", 36).references(Users.id)
    val toUserId = varchar("to_user_id", 36).references(Users.id)
    val fromCity = varchar("from_city", 255)
    val toCity = varchar("to_city", 255)
    val transferredAt = timestamp("transferred_at")

    override val primaryKey = PrimaryKey(id)
}
