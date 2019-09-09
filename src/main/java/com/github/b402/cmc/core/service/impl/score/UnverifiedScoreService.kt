package com.github.b402.cmc.core.service.impl.score

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Score
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object UnverifiedScoreService : DataService<SubmitData>(
        "score_unverifiedScore",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val user = data.getUser() ?: return returnData(ILLEGAL_PERMISSION, "权限不足")
        if (!user.getPermission(gid).await().contains(Permission.PROJECT_JUDGE)) {
            return returnData(ILLEGAL_PERMISSION, "权限不足")
        }
        val scores = Score.getScoreByGame(gid).await() ?: return returnData(ERROR, "数据库异常")
        val array = JsonArray()
        for (score in scores) {
            if (!score.verified) {
                val obj = JsonObject()
                obj.addProperty("uid", score.uid)
                obj.addProperty("score", score.score)
                obj.addProperty("submitJudge", score.submitJudge)
                array.add(obj)
            }
        }
        return returnData(SUCCESS) {
            json.add("list", array)
        }
    }
}