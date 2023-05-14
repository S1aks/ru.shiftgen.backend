package ru.shiftgen.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import ru.shiftgen.databse.authorization.users.Users
import java.util.*

object JWTService {
    private const val secret = "NL7cR4j1DH7zFGi36rvDNAfgVTq9ra5wyzvw4VJfOI4"
    private const val issuer = "Shiftgen.ru"
    const val realm = "Shiftgen API"
    private const val validityAccessTokenInMs = 36_000_00L * 10 // 10 hours
    private const val validityRefreshTokenInMs = 36_000_00L * 480 // 20 day
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withClaimPresence("login")
        .withClaimPresence("structureId")
        .build()

    fun makeToken(login: String, structureId: Int?): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("login", login)
        .withClaim("structureId", structureId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityAccessTokenInMs)
    fun getRefreshExpiration() = System.currentTimeMillis() + validityRefreshTokenInMs
}