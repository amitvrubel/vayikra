package com.vayikra

import com.vayikra.com.vayikra.models.BookJourneyEntryDto
import com.vayikra.models.BookRequestDto
import com.vayikra.models.BookRequestStatus
import com.vayikra.models.CreateBookRequestDto
import com.vayikra.models.UpdateBookRequestStatusDto
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import org.junit.Test

class BookJourneyTest {
    @Test
    fun `journey is still empty before a book is transferred`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val bookId = client.createBook(ownerToken)

            val journey =
                client
                    .get("/books/$bookId/journey") {
                        bearerAuth(ownerToken)
                    }.body<List<BookJourneyEntryDto>>()

            assertEquals(journey.size, 0)
        }

    fun `journey has one entry after book is delivered`() =
        testApplication {
            val client = setupApp()
            val ownerToken = client.registerAndGetToken("owner@test.com")
            val requesterToken = client.registerAndGetToken("requester@test.com")
            val bookId = client.createBook(ownerToken)

            val requestId =
                client
                    .post("/books/$bookId/requests") {
                        contentType(ContentType.Application.Json)
                        bearerAuth(requesterToken)
                        setBody(CreateBookRequestDto())
                    }.body<BookRequestDto>()
                    .id

            listOf(
                ownerToken to BookRequestStatus.ACCEPTED,
                ownerToken to BookRequestStatus.SENT,
                requesterToken to BookRequestStatus.DELIVERED,
            ).forEach { (token, status) ->
                client.patch("/requests/$requestId/status") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(UpdateBookRequestStatusDto(status = status))
                }
            }

            val journey =
                client
                    .get("/books/$bookId/journey") {
                        bearerAuth(ownerToken)
                    }.body<List<BookJourneyEntryDto>>()

            assertEquals(journey.size, 1)
            assertEquals(bookId, journey[0].bookId)
        }
}
