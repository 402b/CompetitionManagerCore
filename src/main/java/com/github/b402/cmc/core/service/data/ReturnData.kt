package com.github.b402.cmc.core.service.data

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.google.gson.JsonObject

const val SUCCESS = "success"
const val NO_TOKEN = "no_token"
const val ERROR = "error"
const val ERROR_TOKEN = "error_token"
const val ERROR_TIMTOUT = "error_timeout"

abstract class ReturnData(
        val status: String
) : IData {
    val json = JsonObject()

    init {
        json.addProperty("status", this.status)
    }

    override fun toJson(): String = Configuration.gson.toJson(json)
}

fun returnData(status: String, func: ReturnData.() -> Unit): ReturnData {
    val rd = object : ReturnData(status) {}
    rd.func()
    return rd
}