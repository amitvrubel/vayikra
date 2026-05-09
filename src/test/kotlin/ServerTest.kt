package com.vayikra

import com.vayikra.db.BookJourney
import com.vayikra.db.Books
import com.vayikra.db.Users
import com.vayikra.db.configureDatabase
import com.vayikra.models.AuthResponse
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
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.BeforeClass

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
    }
    return createClient {
        install(ContentNegotiation) { json() }
    }
}

class ServerTest {
    companion object {
        private lateinit var database: Database

        @BeforeClass
        @JvmStatic
        fun setupDatabase() {
            database =
                Database.connect(
                    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                    driver = "org.h2.Driver",
                    user = "test",
                    password = "test",
                )

            transaction(database) {
                SchemaUtils.create(Users, Books, BookJourney)
            }
        }
    }

    @BeforeTest
    fun cleanDatabase() {
        transaction {
            BookJourney.deleteAll()
            Books.deleteAll()
            Users.deleteAll()
        }
    }

    @Test
    fun `test root endpoint`() =
        testApplication {
            val client = setupApp()
            assertEquals(HttpStatusCode.OK, client.get("/").status)
        }

    @Test
    fun `register returns 201 with tokens`() =
        testApplication {
            val client = setupApp()

            val response =
                client.post("/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        RegisterRequest(
                            email = "test@test.de",
                            password = "123456",
                            name = "Test User",
                            city = "Berlin",
                            country = "Germany",
                        ),
                    )
                }

            assertEquals(HttpStatusCode.Created, response.status)
            val body = response.body<AuthResponse>()
            assertNotNull(body.accessToken)
            assertNotNull(body.refreshToken)
            assertEquals("test@test.de", body.user.email)
        }

    @Test
    fun `register with duplicate email returns 409`() =
        testApplication {
            val client = setupApp()

            val req =
                RegisterRequest(
                    email = "duplicate@test.com",
                    password = "123456",
                    name = "Test User",
                    city = "Berlin",
                    country = "Germany",
                )

            client.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(req)
            }

            val response =
                client.post("/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(req)
                }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

    @Test
    fun `create book requires auth`() =
        testApplication {
            val client = setupApp()
            val response =
                client.post("/books") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateBookRequest(title = "Test Book", author = "Test Author"))
                }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `authenticated user can create book`() =
        testApplication {
            val client = setupApp()

            val authResponse =
                client
                    .post("/auth/register") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            RegisterRequest(
                                email = "test@test.de",
                                password = "123456",
                                name = "Book User",
                                city = "Berlin",
                                country = "Germany",
                            ),
                        )
                    }.body<AuthResponse>()

            val response =
                client.post("/books") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(authResponse.accessToken)
                    setBody(
                        CreateBookRequest(
                            title = "Test Book",
                            author = "Test Author",
                        ),
                    )
                }

            assertEquals(HttpStatusCode.Created, response.status)
        }
}
