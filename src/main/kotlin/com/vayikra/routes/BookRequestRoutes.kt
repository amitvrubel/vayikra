package com.vayikra.routes

import com.vayikra.models.BookStatus
import com.vayikra.models.CreateBookRequestDto
import com.vayikra.models.UpdateBookRequestStatusDto
import com.vayikra.models.toDto
import com.vayikra.services.BookRequestService
import com.vayikra.services.BookService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.bookRequestRoutes(
    bookRequestService: BookRequestService,
    bookService: BookService,
) {
    authenticate("auth-jwt") {
        route("/books/{bookId}/requests") {
            post {
                val requesterId =
                    call
                        .principal<JWTPrincipal>()!!
                        .payload
                        .getClaim("userId")
                        .asString()
                val bookId = call.parameters["bookId"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                val book = bookService.getById(bookId) ?: return@post call.respond(HttpStatusCode.NotFound)

                if (book.status != BookStatus.AVAILABLE) {
                    return@post call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("error" to "Book is not available"),
                    )
                }

                if (book.ownerId == requesterId) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Cannot request your own book"),
                    )
                }

                val dto = call.receive<CreateBookRequestDto>()
                val request =
                    bookRequestService.createRequest(
                        bookId = bookId,
                        requesterId = requesterId,
                        ownerId = book.ownerId,
                        message = dto.message,
                    )

                call.respond(HttpStatusCode.Created, request.toDto())
            }
        }

        route("/requests") {
            get("/my") {
                val userId =
                    call
                        .principal<JWTPrincipal>()!!
                        .payload
                        .getClaim("userId")
                        .asString()
                val requests = bookRequestService.getRequestsForUser(userId)
                call.respond(requests.map { it.toDto() })
            }
            get("/incoming") {
                val userId =
                    call
                        .principal<JWTPrincipal>()!!
                        .payload
                        .getClaim("userId")
                        .asString()
                val requests = bookRequestService.getRequestsForOwner(userId)
                call.respond(requests.map { it.toDto() })
            }
            patch("/{requestId}/status") {
                val updaterId =
                    call
                        .principal<JWTPrincipal>()!!
                        .payload
                        .getClaim("userId")
                        .asString()

                val requestId =
                    call.parameters["requestId"]
                        ?: return@patch call.respond(HttpStatusCode.BadRequest)

                val dto = call.receive<UpdateBookRequestStatusDto>()
                val updated =
                    bookRequestService.updateStatus(
                        requestId = requestId,
                        newStatus = dto.status,
                        updaterId = updaterId,
                    )

                when (updated) {
                    null ->
                        call.respond(
                            HttpStatusCode.Forbidden,
                            mapOf("error" to "Access denied"),
                        )
                    else -> call.respond(HttpStatusCode.OK, updated.toDto())
                }
            }
        }
    }
}
