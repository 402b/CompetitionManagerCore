package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JoinGame
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object GameJoinInfoService : DataService<RequestJoinInfo>(
        "game_joinInfo",
        Permission.VERIFIED,
        RequestJoinInfo::class.java
) {
    override suspend fun onRequest(data: RequestJoinInfo): ReturnData {
        val djoined = JoinGame.getJoinedInfo(data.gid, data.verified)
        val user = data.getUser() ?: return returnData(ILLEGAL_PERMISSION,"权限不足")
        if(!user.getPermission(data.gid).await().contains(Permission.JUDGE)){
            return returnData(ILLEGAL_PERMISSION,"权限不足")
        }
        val joined = djoined.await()
                ?: return returnData(ERROR, "数据库异常")
        return returnData(SUCCESS) {
            val arr = JsonArray()
            for (uid in joined) {
                arr.add(uid)
            }
            json.add("uids", arr)
        }
    }
}

class RequestJoinInfo(json: JsonObject) : SubmitData(json) {
    val gid = super.json.getInt("gid")
    val verified = super.json.getBoolean("verified", false)
}