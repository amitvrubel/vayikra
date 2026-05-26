package com.vayikra.plugins

import com.vayikra.com.vayikra.routes.bookJourneyRoutes
import com.vayikra.com.vayikra.services.BookJourneyService
import com.vayikra.com.vayikra.services.JwtService
import com.vayikra.routes.authRoutes
import com.vayikra.routes.bookRequestRoutes
import com.vayikra.routes.bookRoutes
import com.vayikra.services.BookRequestService
import com.vayikra.services.BookService
import com.vayikra.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val userService = UserService()
    val bookService = BookService()
    val bookRequestService = BookRequestService()
    val bookJourneyService = BookJourneyService()
    val jwtService =
        JwtService(
            secret = environment.config.property("jwt.secret").getString(),
            issuer = environment.config.property("jwt.issuer").getString(),
            audience = environment.config.property("jwt.audience").getString(),
        )
    routing {
        get("/") {
            call.respondText("Vayikra API is running")
        }
        authRoutes(userService, jwtService)
        bookRoutes(bookService)
        bookRequestRoutes(bookRequestService, bookService)
        bookJourneyRoutes(bookJourneyService)
    }
}
