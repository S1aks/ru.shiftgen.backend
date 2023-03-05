package ru.shiftgen.features.auth.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceive(
    val login: String,
    val password: String
)

@Serializable
data class RefreshReceive(
    val login: String,
    val refreshToken: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)