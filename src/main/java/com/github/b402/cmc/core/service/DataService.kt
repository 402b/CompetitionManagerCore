package com.github.b402.cmc.core.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.token.Token
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import org.apache.log4j.Logger

abstract class DataService<in S : SubmitData>(
        val path: String,
        val permission: Permission,
        val sClass: Class<in S>
) {
    abstract suspend fun readData(data: S): ReturnData

    open suspend fun input(data: String): ReturnData {
        val je = jsonParser.parse(data) ?: return returnData(ERROR, "状态异常,传入数据非json")
        if (!je.isJsonObject) {
            return returnData(ERROR, "状态异常,传入数据非json")
        }
        val json = je.asJsonObject
        if (this.permission != Permission.ANY && !json.has("token")) {
            return returnData(NO_TOKEN, "状态异常,未传入token")
        }
        val data = json.getAsJsonObject("Data") ?: return returnData(ERROR, "状态异常,未传入数据")
        val c = sClass.getConstructor(JsonObject::class.java)
        val input: S
        try {
            input = c.newInstance(data) as S
        } catch (e: Throwable) {
            Logger.getLogger(DataService::class.java).debug("传入的参数异常", e)
            return returnData(ILLEGAL_INPUT, "传入的参数异常")
        }
        if (this.permission != Permission.ANY) {
            val token = Token.deToken(json.get("token").asString)
                    ?: return returnData(ERROR_TOKEN, "token校验失败")
            input.token = token
        }
        return readData(input)
    }

    companion object {
        val jsonParser = JsonParser()
    }
}