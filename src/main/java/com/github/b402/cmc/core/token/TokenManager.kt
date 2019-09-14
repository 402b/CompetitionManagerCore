package com.github.b402.cmc.core.token

import com.github.b402.cmc.core.FileManager
import com.github.b402.cmc.core.util.hashSHA256
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.random.Random

object TokenManager {
    //    const val TOURIST_TOKEN = ""
    lateinit var Signature: String

    @JvmStatic
    fun init() {
        val f = File(FileManager.getBaseFolder(), "TokenSignature")
        if (!f.exists()) {
            f.createNewFile()
            val fw = FileWriter(f)
            fw.write("${UUID.randomUUID()}/${Math.random()}/${Random.nextLong()}".hashSHA256())
            fw.flush()
            fw.close()
        }
        val fr = FileReader(f)
        Signature = fr.readText()
        fr.close()
    }
}