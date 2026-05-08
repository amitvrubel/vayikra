package com.vayikra.routes

import com.vayikra.com.vayikra.services.JwtService
import com.vayikra.models.AuthResponse
import com.vayikra.models.LoginRequest
import com.vayikra.models.RegisterRequest
import com.vayikra.services.UserService

import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes(userService: UserService, jwtService: JwtService) {
    route("/auth") {
        post("/register") {
            val req = call.receive<RegisterRequest>()
            if (userService.findByEmail(req.email) != null) {
                return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already in use"))
            }
            val user = userService.createUser(
                email = req.email,
                name = req.name,
                password = req.password,
                city = req.city,
                country = req.country,
            )

            call.respond(
                HttpStatusCode.Created,
               AuthResponse(
                    accessToken = jwtService.generateAccessToken(user.id),
                    refreshToken = jwtService.generateRefreshToken(user.id),
                   user = user
                ))
        }

        post("/login") {
            val req = call.receive<LoginRequest>()
            val user = userService.findByEmail(req.email) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            if (!BCrypt.checkpw(req.password, user.passwordHash)) {
                return@post call.respond(HttpStatusCode.Unauthorized)
            }

            call.respond(AuthResponse(
                accessToken = jwtService.generateAccessToken(user.id),
                refreshToken = jwtService.generateRefreshToken(user.id),
                user = user
            ))

        }
    }

    authenticate("auth-jwt") {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asString()
            val user = userService.findById(userId) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(user)
        }
    }
}