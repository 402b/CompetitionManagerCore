package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object GameResultService : DataService<SubmitData>(
        "game_result",
        Permission.USER,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        if (data.json.getBoolean("all", false)) {
            val resultList = Game.getAllGameResult().await() ?: return returnData(ERROR, "数据库异常")
            val array = JsonArray()
            for (result in resultList) {
                val json = JsonObject()
                json.addProperty("gid", result.gid)
                json.add("result", result.resultJson)
                json.addProperty("time", result.time)
                array.add(json)
            }
            return returnData(SUCCESS) {
                json.add("results", array)
            }
        }
        val gid = data.json.getInt("gid")
        val result = Game.getGameResult(gid).await()
                ?: return returnData(ERROR, "这个比赛还没有结果")
        return returnData(SUCCESS) {
            json.addProperty("gid", gid)
            json.add("result", result.resultJson)
            json.addProperty("time", result.time)
        }
    }
}