package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.GameResult
import com.google.gson.JsonArray
import kotlinx.coroutines.Job

object GameListService : DataService<SubmitData>(
        "game_list",
        Permission.ANY,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val archive = data.json.getBoolean("archive", false)
        if (archive) {
            val user = data.token?.getUser() ?: return returnData(ILLEGAL_PERMISSION, "权限不足")
            if (!user.permission.contains(Permission.MAIN_JUDGE)) {
                return returnData(ILLEGAL_PERMISSION, "权限不足")
            }
        }
        val allGames = Game.getAllGames(archive).await() ?: return returnData(ERROR, "数据库错误")
        return returnData(SUCCESS) {
            val ja = JsonArray()
            for (g in allGames) {
                ja.add(g)
            }
            this.json.add("gamelist", ja)
        }
    }

}