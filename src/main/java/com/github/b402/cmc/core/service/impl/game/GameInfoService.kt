package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.min

object GameInfoService : DataService<SubmitData>(
        "game_info",
        Permission.ANY,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val ids = data.json.getNumberList("gameId") ?: return returnData("ERROR", "传入参数过少")
        val array = JsonArray()
        val jobs = mutableListOf<Job>()
        for (i in 0 until min(ids.size, 10)) {
            val id = ids[i].toInt()
            jobs += GlobalScope.launch(coroutineContext) {
                val game = Game.getGame(id).await()
                val jo = JsonObject()
                jo.addProperty("id", id)
                if (game == null) {
                    jo.addProperty("status", ERROR)
                    jo.addProperty("reason", "找不到这个比赛")
                } else {
                    jo.addProperty("status", SUCCESS)
                    jo.addProperty("name", game.name)
                    jo.addProperty("type", game.type.key)
                    jo.addProperty("time", game.time)
                    jo.addProperty("startTime", game.startTime)
                    jo.addProperty("endTime", game.endTime)
                    jo.addProperty("amount", game.amount)
                }
                array.add(jo)
            }
        }
        for (job in jobs) {
            job.join()
        }
        /*
        * {
        *   "info": [
        *       {
        *           "id": 1,
        *           "status": "error",
        *           "reason": "找不到这个比赛"
        *       },
        *       {
        *           "id": 2,
        *           "status": "success",
        *           "name": "项目名字",
        *           "time": 时间(long),
        *           "startTime": 开始时间(long),
        *           "endTime": 结束时间(long),
        *           "amount": 最多参赛名额(int)
        *       }
        *   ]
        * }
        *
        * */
        return returnData(SUCCESS) {
            json.add("info", array)
        }
    }

}