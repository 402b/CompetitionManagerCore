package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JoinGame
import com.github.b402.cmc.core.sql.data.User
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.min

object JoinedGameService :DataService<SubmitData>(
        "game_getJoined",
        Permission.USER,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        if(data.json.contains("ids")){
            val user = data.token?.getUser() ?: return returnData(ILLEGAL_PERMISSION, "权限不足")
            if(!user.permission.contains(Permission.ADMIN)){
                return returnData(ILLEGAL_PERMISSION, "权限不足")
            }
            val ids = data.json.getNumberList("ids")!!
            val array = JsonArray()
            val jobs = mutableListOf<Job>()
            for (i in 0 until min(ids.size, 10)) {
                val id = ids[i].toInt()
                jobs += GlobalScope.launch(coroutineContext) {
                    val json = JsonObject()
                    json.addProperty("uid", id)
                    val gid = JoinGame.getJoinedGame(id).await()
                    if(gid == null){
                        json.addProperty("status", ERROR)
                        json.addProperty("reason", "数据库异常")
                    }else{
                        json.addProperty("status", SUCCESS)
                        val gidArr = JsonArray()
                        gid.forEach { gidArr.add(it) }
                        json.add("gids",gidArr)
                    }
                    array.add(json)
                }
            }
            for(job in jobs){
                job.join()
            }
            return returnData(SUCCESS) {
                json.add("info", array)
            }
        }else{
            val gid = JoinGame.getJoinedGame(data.token?.uid ?: return returnData(ERROR,"用户异常")
            ).await()
            return returnData(SUCCESS){
                if(gid == null){
                    json.addProperty("status", ERROR)
                    json.addProperty("reason", "数据库异常")
                }else{
                    json.addProperty("status", SUCCESS)
                    val gidArr = JsonArray()
                    gid.forEach { gidArr.add(it) }
                    json.add("gids",gidArr)
                }
            }
        }
    }

}