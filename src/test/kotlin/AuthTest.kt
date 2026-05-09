package com.vayikra

import com.vayikra.models.AuthResponse
import com.vayikra.models.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthTest {
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
    fun `debug register`() =
        testApplication {
            val client = setupApp()
            println("STARTING DEBUG TEST")
            val response =
                client.post("/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        RegisterRequest(
                            email = "debug@test.com",
                            password = "123456",
                            name = "Debug",
                            city = "Berlin",
                            country = "Germany",
                        ),
                    )
                }
            println("STATUS: ${response.status}")
            println("BODY: ${response.body<String>()}")
        }
}
