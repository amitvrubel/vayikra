package com.vayikra.com.vayikra.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val secret: String = "tmp-vayikra-secret-that-will-not-reach-production",
    private val issuer: String = "vayikra",
    private val audience: String = "vayikra-users",
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(userId: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 15 * 60 * 1000))
        .sign(algorithm)

    fun generateRefreshToken(userId: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withClaim("type", "refresh")
        .withExpiresAt(Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
        .sign(algorithm)

    fun verifier() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun getAudience() = audience
    fun getIssuer() = issuer
}