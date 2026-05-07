package com.vayikra.plugins

import UserService
import com.vayikra.com.vayikra.routes.authRoutes
import com.vayikra.com.vayikra.services.JwtService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userService = UserService()
    val jwtService = JwtService()
    routing {
        get("/") {
            call.respondText("Vayikra API is running")
        }
        authRoutes(userService, jwtService)
    }
}