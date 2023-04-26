package ru.shiftgen.features.authorization.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String,
    val group: Int,
    val structureId: Int
)

@Serializable
data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String
)