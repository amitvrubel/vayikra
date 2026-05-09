package com.vayikra

import com.vayikra.db.BookJourney
import com.vayikra.db.BookRequests
import com.vayikra.db.Books
import com.vayikra.db.Users
import com.vayikra.db.configureDatabase
import com.vayikra.models.AuthResponse
import com.vayikra.models.BookDto
import com.vayikra.models.CreateBookRequest
import com.vayikra.models.RegisterRequest
import com.vayikra.plugins.configureRouting
import com.vayikra.plugins.configureSecurity
import com.vayikra.plugins.configureSerialization
import com.vayikra.plugins.configureStatusPages
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun ApplicationTestBuilder.setupApp(): HttpClient {
    environment {
        config = ApplicationConfig("application.yaml")
    }
    application {
        configureSerialization()
        configureDatabase()
        configureSecurity()
        configureStatusPages()
        configureRouting()

        transaction {
            BookJourney.deleteAll()
            BookRequests.deleteAll()
            Books.deleteAll()
            Users.deleteAll()
        }
    }
    return createClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

suspend fun HttpClient.registerAndGetToken(email: String): String {
    val response =
        post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = email,
                    password = "123456",
                    name = "Test User",
                    city = "Berlin",
                    country = "Germany",
                ),
            )
        }
    check(response.status == HttpStatusCode.Created) {
        "Register failed with status ${response.status}"
    }
    return response.body<AuthResponse>().accessToken
}

suspend fun HttpClient.createBook(token: String): String =
    post("/books") {
        contentType(ContentType.Application.Json)
        bearerAuth(token)
        setBody(CreateBookRequest(title = "Test Book", author = "Test Author"))
    }.body<BookDto>().id
