package com.github.b402.cmc.core.token

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.util.deBase64
import com.github.b402.cmc.core.util.hashSHA256WithSalt
import com.github.b402.cmc.core.util.toBase64
import kotlinx.coroutines.*
import kotlin.random.Random


class Token(
        val uid: Int,
        val exp: Long = System.currentTimeMillis() + 60L * 1000L,
        val iat: Long = System.currentTimeMillis(),
        val jti: Long = Random.Default.nextLong()
) {

    suspend fun getUser() = getUserCache(uid)

    init {
        cache(uid)
    }

    fun toTokenString(): String {
        println("生成token: ${"""{"uid":$uid,"exp":$exp,"iat":$iat,"jti":$jti}"""}")
        val json = """{"uid":$uid,"exp":$exp,"iat":$iat,"jti":$jti}""".trimIndent().toBase64()
        val auth = "$HEADER,$json".hashSHA256WithSalt("$uid")
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
        fun clearCache(uid: Int){
            userCache.remove(uid)
        }

        private val userCache = mutableMapOf<Int, UserCache>()
        private fun cache(uid: Int) {
            GlobalScope.launch {
                val cache = userCache[uid]
                if (cache != null) {
                    if (System.currentTimeMillis() - cache.time <= 30000) {
                        return@launch
                    }
                }
                val user = User.getUser(uid).await()
                if (user != null) {
                    userCache[uid] = UserCache(user, System.currentTimeMillis())
                }
            }
        }

        private suspend fun getUserCache(uid: Int): User {
            val cache = userCache[uid]
            if (cache != null) {
                if (System.currentTimeMillis() - cache.time <= 30000) {
                    return cache.user
                }
            }
            val user = User.getUser(uid).await()
            if (user != null) {
                userCache[uid] = UserCache(user, System.currentTimeMillis())
                return user
            } else {
                throw IllegalStateException("数据库状态异常")
            }
        }

        const val HEADER = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

        fun deToken(token: String): Token? {
            val str = token.split("\\.".toRegex(), 3)
            if (str.size != 3 || str[0] != HEADER) {
                return null
            }
            val json = str[1].deBase64()
            val data = Configuration(json)
            val auth = "${str[0]},${str[1]}".hashSHA256WithSalt("${data.getInt("uid")}")
            if (auth == str[2]) {
                val token = Token(
                        data.getInt("uid"),
                        data.getLong("exp"),
                        data.getLong("iat"),
                        data.getLong("jti")
                )
                if (token.exp < System.currentTimeMillis()) {
                    return Token(token.uid)
                }
                return token
            }
            return null
        }

    }


}

data class UserCache(val user: User, val time: Long)