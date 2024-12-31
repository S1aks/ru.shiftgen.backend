package ru.shiftgen.databse.authorization.tokens

data class TokenDTO(
    val login: String,
    val accessToken: String,
    val refreshToken: String
)