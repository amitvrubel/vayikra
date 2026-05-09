package com.vayikra

import com.vayikra.models.BookRequestDto
import com.vayikra.models.BookRequestStatus
import com.vayikra.models.CreateBookRequestDto
import com.vayikra.models.UpdateBookRequestStatusDto
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class BookRequestTest {
    @Test
    fun `user can request an available book`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val requesterToken = client.registerAndGetToken("requester@test.com")
            val bookId = client.createBook(ownerToken)

            val response =
                client.post("/books/$bookId/requests") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(requesterToken)
                    setBody(CreateBookRequestDto(message = "Test message!"))
                }
            assertEquals(HttpStatusCode.Created, response.status)
        }

    @Test
    fun `user cannot request their own book`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val bookId = client.createBook(ownerToken)

            val response =
                client.post("/books/$bookId/requests") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(ownerToken)
                    setBody(CreateBookRequestDto())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun `book request requires auth`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val bookId = client.createBook(ownerToken)

            val response =
                client.post("/books/$bookId/requests") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateBookRequestDto())
                }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `owner can accept a book request`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val requesterToken = client.registerAndGetToken("requester@test.com")
            val bookId = client.createBook(ownerToken)

            val requestResponse =
                client
                    .post("/books/$bookId/requests") {
                        contentType(ContentType.Application.Json)
                        bearerAuth(requesterToken)
                        setBody(CreateBookRequestDto())
                    }.body<BookRequestDto>()

            val response =
                client.patch("/requests/${requestResponse.id}/status") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(ownerToken)
                    setBody(UpdateBookRequestStatusDto(status = BookRequestStatus.ACCEPTED))
                }
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `requester can cancel a book request`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val requesterToken = client.registerAndGetToken("requester@test.com")
            val bookId = client.createBook(ownerToken)

            val requestResponse =
                client
                    .post("/books/$bookId/requests") {
                        contentType(ContentType.Application.Json)
                        bearerAuth(requesterToken)
                        setBody(CreateBookRequestDto())
                    }.body<BookRequestDto>()

            val response =
                client.patch("/requests/${requestResponse.id}/status") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(requesterToken)
                    setBody(UpdateBookRequestStatusDto(status = BookRequestStatus.CANCELLED))
                }
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `random user cannot update request status`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val requesterToken = client.registerAndGetToken("requester@test.com")
            val randomToken = client.registerAndGetToken("random@test.com")
            val bookId = client.createBook(ownerToken)

            val requestResponse =
                client
                    .post("/books/$bookId/requests") {
                        contentType(ContentType.Application.Json)
                        bearerAuth(requesterToken)
                        setBody(CreateBookRequestDto())
                    }.body<BookRequestDto>()

            val response =
                client.patch("/requests/${requestResponse.id}/status") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(randomToken)
                    setBody(UpdateBookRequestStatusDto(status = BookRequestStatus.CANCELLED))
                }
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
}
