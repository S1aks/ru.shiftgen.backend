package ru.shiftgen.features.authorization.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class AccessRequest(
    val accessToken: String
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)