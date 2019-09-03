@file:JvmName("Util")
package com.github.b402.cmc.core.util

import com.github.b402.cmc.core.token.TokenManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.*
import kotlin.coroutines.coroutineContext

fun String.toBase64(): String {
    return String(Base64.getUrlEncoder().withoutPadding().encode(this.toByteArray()))
}

fun String.deBase64(): String {
    return String(Base64.getUrlDecoder().decode(this))
}

val instance = MessageDigest.getInstance("MD5")
fun String.md5Hash(): String {
    val ba = instance.digest("$this-cmc".toByteArray())
    return byteArrayToHexString(ba)
}

fun String.md5HashWithSalt(salt: String = TokenManager.Signature): String {
    val ba = instance.digest("$this-$salt-cmc".toByteArray())
    return byteArrayToHexString(ba)
}

private val hexDigIts = "0123456789ABCDEF".toCharArray()
fun byteArrayToHexString(b: ByteArray): String {
    val resultSb = StringBuffer()
    for (i in b.indices) {
        resultSb.append(byteToHexString(b[i]))
    }
    return resultSb.toString()
}

fun byteToHexString(b: Byte): String {
    var n = b.toInt()
    if (n < 0) {
        n += 256
    }
    val d1 = n / 16
    val d2 = n % 16
    return hexDigIts[d1] + "" + hexDigIts[d2]
}
@Deprecated("已经不需要使用Channel了")
suspend fun <E> Channel<E>.asyncSend(e: E) {
    val c = this
    GlobalScope.launch(coroutineContext) {
        if (c.isClosedForSend) return@launch
        c.send(e)
        c.close()
    }
}