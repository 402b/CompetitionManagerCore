package com.github.b402.cmc.core.service.data

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.token.Token
import com.google.gson.JsonObject

const val SUCCESS = "success"
const val NO_TOKEN = "no_token"
const val ERROR = "error"
const val ERROR_TOKEN = "error_token"
const val ERROR_TIMTOUT = "error_timeout"
const val ILLEGAL_INPUT = "illegal_input"
const val INVALID_USER_OR_PASSWORD = "invalid_user_or_password"

open class ReturnData(
        val status: String
) : IData {
    val json = JsonObject()

    init {
        json.addProperty("status", this.status)
    }

    override fun toJson(): String = Configuration.gson.toJson(json)
    override fun toString(): String {
        return "ReturnData(json=$json)"
    }
}

inline fun returnData(status: String, token: Token, func: ReturnData.() -> Unit): ReturnData {
    val rd = ReturnData(status)
    rd.json.addProperty("token", token.toTokenString())
    rd.func()
    return rd
}

inline fun returnData(status: String, func: ReturnData.() -> Unit): ReturnData {6
    val rd = ReturnData(status)
    rd.func()
    return rd
}

 fun returnData(status: String, reason: String): ReturnData = returnData(status) {
    this.json.addProperty("reason", reason)
}