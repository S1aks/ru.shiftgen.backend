package ru.shiftgen.features.auth.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceive(
    val login: String,
    val email: String,
    val password: String,
    val group: Int
)

@Serializable
data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String
)