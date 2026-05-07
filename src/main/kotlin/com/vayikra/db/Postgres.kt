package com.vayikra.db

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

fun Application.configureDatabase() {
    val url = environment.config.property("db.url").getString()
    val user = environment.config.property("db.user").getString()
    val password = environment.config.property("db.password").getString()


    Database.connect(url = url, driver = "org.postgresql.Driver",  user = user, password = password)
    transaction {
        SchemaUtils.create(Users, Books, BookJourney)
    }

    log.info("Database configured")
}