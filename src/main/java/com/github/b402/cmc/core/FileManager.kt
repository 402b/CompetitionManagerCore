package com.github.b402.cmc.core

import java.io.File

object FileManager {
    const val HELLO_WORLD ="Hello world"
    fun checkFolder(){
        val bf = getBaseFolder()
        if(!bf.exists()){
            bf.mkdirs()
        }
        val mf = File(bf,"module")
        if(!mf.exists()){
            mf.mkdir()
        }
        val cf = File(bf,"config")
        if(!cf.exists()){
            cf.mkdir()
        }
    }

    fun getBaseFolder() = File("CompetitionManagerCore")

}