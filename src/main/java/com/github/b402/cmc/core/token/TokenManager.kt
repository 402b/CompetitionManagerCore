package com.github.b402.cmc.core.token

import com.github.b402.cmc.core.FileManager
import com.github.b402.cmc.core.util.md5Hash
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.random.Random

object TokenManager {
//    const val TOURIST_TOKEN = ""
    lateinit var Signature: String
    lateinit var Tourist_Token :Token

    fun init() {
        val f = File(FileManager.getBaseFolder(),"TokenSignature")
        if(!f.exists()){
            f.createNewFile()
            val fw = FileWriter(f)
            fw.write("${UUID.randomUUID()}/${Math.random()}/${Random.nextLong()}".md5Hash())
            fw.flush()
            fw.close()
        }
        val fr = FileReader(f)
        Signature = fr.readText()
        fr.close()
        Tourist_Token = Token(-1,0,0,0)
    }
}