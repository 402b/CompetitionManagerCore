package com.github.b402.cmc.core.service.data

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.token.Token
import com.google.gson.JsonObject

open class SubmitData(
        jsonObject: JsonObject
) : IData {
    val json: ConfigurationSection = Configuration(jsonObject)
    var token: Token? = null

    fun hasToken(): Boolean = json.getString("token") != null

    fun getTokenString(): String? = json.getString("token")


    override fun toJson(): String = json.toJson()
}