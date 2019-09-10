package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JoinGame
import com.google.gson.JsonArray

object UnverifiedJoinGameInfoService:DataService<SubmitData>(
        "game_unverifiedJoin",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val user = data.getUser()
        if(user == null || !user.getPermission(gid).await().contains(Permission.JUDGE)){
            return returnData(ILLEGAL_PERMISSION,"权限不足")
        }
        val list = JoinGame.getJoinedInfo(gid,false,false).await() ?: return returnData(ERROR,"数据库异常")
        val array = JsonArray()
        for(id in list) array.add(id)
        return returnData(SUCCESS){
            json.add("uids",array)
        }
    }
}