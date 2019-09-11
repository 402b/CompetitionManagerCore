package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserVerifyJoinGameEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JoinGame
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object VerifyJoinGameService:DataService<SubmitData>(
        "game_verifyJoinGame",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val user = data.getUser()
        if (user == null || !user.getPermission(gid).await().contains(Permission.JUDGE)) {
            return returnData(ERROR, "权限不足")
        }
        val verify = data.json.getBoolean("verify", false)
        val uids = data.json.getNumberList("checked") ?: return returnData(ILLEGAL_INPUT, "参数错误")
        val result = JsonArray()
        val jobs = mutableListOf<Job>()
        for(id in uids){
            jobs += GlobalScope.launch(GlobalScope.coroutineContext) {
                val obj = JsonObject()
                val uid = id.toInt()
                obj.addProperty("uid", uid)
                if (!JoinGame.verifyJoin(uid,gid,verify).await()) {
                    obj.addProperty("status", ERROR)
                    obj.addProperty("reason", "数据库异常")
                } else {
                    val uvjge = UserVerifyJoinGameEvent(gid,uid,user.uid,verify)
                    EventBus.callEvent(uvjge)
                    obj.addProperty("status", SUCCESS)
                }
                result.add(obj)
            }
        }
        for(job in jobs){
            job.join()
        }
        return returnData(SUCCESS) {
            this.json.add("result", result)
        }
    }
}