package com.vayikra.com.vayikra.routes

import com.vayikra.com.vayikra.models.toDto
import com.vayikra.com.vayikra.services.BookJourneyService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.bookJourneyRoutes(bookJourneyService: BookJourneyService) {
    route("/books/{bookId}/journey") {
        get {
            val bookId =
                call.parameters["bookId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

            val journey = bookJourneyService.getJourneyForBook(bookId)
            call.respond(journey.map { it.toDto() })
        }
    }
}
