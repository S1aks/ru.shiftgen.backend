package ru.shiftgen.databse.access.tokens

data class TokenDTO(
    val login: String,
    val accessToken: String,
    val refreshToken: String
)