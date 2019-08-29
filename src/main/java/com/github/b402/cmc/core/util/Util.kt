package com.github.b402.cmc.core.util

import com.github.b402.cmc.core.token.TokenManager
import java.security.MessageDigest
import java.util.*

fun String.toBase64(): String {
    return String(Base64.getEncoder().encode(this.toByteArray()))
}
fun String.deBase64():String{
    return String(Base64.getDecoder().decode(this))
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

private val hexDigIts = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
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