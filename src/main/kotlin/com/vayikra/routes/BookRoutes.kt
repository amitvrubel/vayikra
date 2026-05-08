package com.vayikra.routes

import com.vayikra.com.vayikra.models.CreateBookRequest
import com.vayikra.services.BookService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.bookRoutes(bookService: BookService) {
    route("/books") {
        get {
            val books = bookService.getAllAvailable()
            call.respond(books)
        }

        authenticate("auth-jwt") {
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asString()
                val req = call.receive<CreateBookRequest>()

                val book =
                    bookService.createBook(
                        ownerId = userId,
                        title = req.title,
                        author = req.author,
                        isbn = req.isbn,
                        imageUrl = req.imageUrl,
                        notes = req.notes,
                    )
                call.respond(book)
            }

            get("/my") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asString()
                val books = bookService.getBooksForUser(userId)
                call.respond(books)
            }
        }
    }
}
