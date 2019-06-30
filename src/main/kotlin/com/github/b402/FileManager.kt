package com.github.b402

import java.io.File

object FileManager {
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