package ru.shiftgen.databse.access.tokens

sealed interface TokenState {
    enum class ErrorCodes {
        ERROR_CREATE,
        ERROR_UPDATE
    }

    data class Success(val data: TokenDTO) : TokenState
    data class Error(val statusCode: ErrorCodes) : TokenState
}