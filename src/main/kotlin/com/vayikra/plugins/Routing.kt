package com.vayikra.plugins

import com.vayikra.com.vayikra.services.JwtService
import com.vayikra.routes.authRoutes
import com.vayikra.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val userService = UserService()
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
    }
}
