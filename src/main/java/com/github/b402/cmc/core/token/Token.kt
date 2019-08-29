package com.github.b402.cmc.core.token

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.util.deBase64
import com.github.b402.cmc.core.util.md5HashWithSalt
import com.github.b402.cmc.core.util.toBase64
import java.util.*
import kotlin.random.Random


class Token(
        val uid: Int,
        val exp: Long = System.currentTimeMillis() + 60L * 1000L,
        val iat: Long = System.currentTimeMillis(),
        val jti: Long = Random.Default.nextLong()
) {


    val isTourist: Boolean by lazy {
        this == TokenManager.Tourist_Token
    }

    fun toTokenString(): String {
        val json = """{"uid":$uid,"exp":$exp,"iat":$iat,"jti":$jti}""".trimIndent().toBase64()
        val auth = "$HEADER,$json".md5HashWithSalt()
        return "$HEADER.$json.$auth"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (uid != other.uid) return false
        if (exp != other.exp) return false
        if (iat != other.iat) return false
        if (jti != other.jti) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + iat.hashCode()
        result = 31 * result + jti.hashCode()
        return result
    }

    override fun toString(): String {
        return "Token(uid=$uid, exp=$exp, iat=$iat, jti=$jti)"
    }

    companion object {
        const val HEADER = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

        suspend fun deToken(token: String): Token? {
            val str = token.split("\\.".toRegex(), 3)
            if (str.size != 3 || str[0] != HEADER) {
                return null
            }
            val auth = "${str[0]},${str[1]}".md5HashWithSalt()
            if (auth == str[2]) {
                val json = str[1].deBase64()
                val data = Configuration(json)
                val token = Token(
                        data.getInt("uid"),
                        data.getLong("exp"),
                        data.getLong("iat"),
                        data.getLong("jti")
                )
                if (token.exp < System.currentTimeMillis()) {
                    return Token(token.uid)
                }

            }
            return null
        }

    }


}