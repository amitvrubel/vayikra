package com.vayikra

import com.vayikra.models.AuthResponse
import com.vayikra.models.CreateBookRequest
import com.vayikra.models.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class BookTest {
    @Test
    fun `test root endpoint`() =
        testApplication {
            val client = setupApp()
            assertEquals(HttpStatusCode.OK, client.get("/").status)
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
                    setBody(CreateBookRequest(title = "Test Book", author = "Test Author"))
                }
            assertEquals(HttpStatusCode.Created, response.status)
        }
}
