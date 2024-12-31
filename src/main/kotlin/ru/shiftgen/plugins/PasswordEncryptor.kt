package ru.shiftgen.plugins

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object PasswordEncryptor {
    private const val SECRET_KEY = "7763882190"
    private const val ALGORITHM = "HmacSHA1"
    private val HASH_KEY = hex(SECRET_KEY)
    private val HMAC_KEY = SecretKeySpec(HASH_KEY, ALGORITHM)

    fun hash(password: String): String {
        val hmac = Mac.getInstance(ALGORITHM).apply {
            init(HMAC_KEY)
        }
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }
}