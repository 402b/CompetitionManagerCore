package com.github.b402.cmc.core

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileManager {
    fun checkFolder() {
        val bf = getBaseFolder()
        println(bf.absolutePath)
        if (!bf.exists()) {
            bf.mkdirs()
        }
        val mf = File(bf, "module")
        if (!mf.exists()) {
            mf.mkdir()
        }
        val cf = File(bf, "config")
        if (!cf.exists()) {
            cf.mkdir()
        }
    }

    fun getBaseFolder() = File("CompetitionManagerCore")

    fun saveResources(file: File, ins: InputStream) {
        if(file.exists()){
            file.delete()
        }
        file.createNewFile()
        val fos = FileOutputStream(file)
        while(true){
            val i = ins.read();
            if(i == -1){
                break
            }
            fos.write(i)
        }
        fos.flush()
        fos.close()
    }
}