package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.sql.data.GameType
import com.google.gson.JsonObject

object CreateGameService : DataService<CreateGameData>(
        "createGame",
        Permission.ADMIN,
        CreateGameData::class.java
) {
    override suspend fun onRequest(data: CreateGameData): ReturnData {
        TODO("not implemented")
    }
}

class CreateGameData(json: JsonObject) : SubmitData(json) {
    val name = json.get("name").asString
    val type = GameType.getGameType(json.get("type").asString)
    val time = json.get("time").asLong
    val amount = json.get("number").asInt
    val startTime = json.get("startDate").asLong
    val endTime = json.get("endDate").asLong
}