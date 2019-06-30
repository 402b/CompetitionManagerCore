package com.github.b402.configuration

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class Configuration : MemorySection {

    companion object{
        val gson:Gson = Gson()
    }
    constructor(json: String) : super(JsonParser().parse(json).asJsonObject)

    constructor(file: File) : super() {
        val reader = FileReader(file)
        super.init(JsonParser().parse(reader).asJsonObject)
    }

    constructor(json: JsonObject) : super(json)

    fun save(file: File) {
        val obj = this.save()
        if(file.exists()){
            file.delete()
        }
        if(!file.exists()){
            val par = file.parentFile
            if(!par.exists()){
                par.mkdirs()
            }
            file.createNewFile()
        }
        val fw = FileWriter(file)
        val bw = BufferedWriter(fw)
        val save = gson.toJson(obj)
        bw.write(save)
        bw.flush()
        bw.close()
        fw.close()
    }


}
