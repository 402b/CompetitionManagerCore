package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JudgeInfo
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred

object JudgeInfoService : DataService<SubmitData>(
        "judge_info",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    /*
    Data: {
        by: 'game',
        //by: 'user',
        id: 123,
        verified: true
    }
     */
    override suspend fun onRequest(data: SubmitData): ReturnData {
        if (data.json.contains("by")) {
            val user = data.token?.getUser() ?: return returnData(ERROR, "权限不足")
            val verified = data.json.getBoolean("verified", true)
            val id = data.json.getInt("id")
            val infos: List<JudgeInfo>
            when (data.json.getString("by")) {
                "game" -> {//通过比赛搜寻裁判
                    infos = JudgeInfo.getJudgeInfoByGameID(id, verified).await()
                            ?: return returnData(ERROR, "数据库异常")
                }
                "user" -> {
                    if(!user.permission.contains(Permission.MAIN_JUDGE)){
                        return returnData(ERROR, "权限不足")
                    }
                    infos = JudgeInfo.getJudgeInfo(id, verified).await()
                            ?: return returnData(ERROR, "数据库异常")
                }
                else -> {
                    return returnData(ILLEGAL_INPUT, "参数错误")
                }
            }
            return returnData(SUCCESS) {
                val arr = JsonArray()
                for (ji in infos) {
                    val obj = JsonObject()
                    obj.addProperty("uid",ji.uid)
                    obj.addProperty("gid", ji.gid)
                    obj.addProperty("type", ji.type.display)
                    obj.addProperty("verified", ji.verified)
                    arr.add(obj)
                }
                json.add("infos", arr)
            }
        }
        val uid = data.token!!.uid
        val judgeInfos = JudgeInfo.getJudgeInfo(uid).await() ?: return returnData(ERROR, "数据库异常")

        return returnData(SUCCESS) {
            val ja = JsonArray()
            for (ji in judgeInfos) {
                val obj = JsonObject()
                obj.addProperty("uid",ji.uid)
                obj.addProperty("gid", ji.gid)
                obj.addProperty("type", ji.type.display)
                obj.addProperty("verified", ji.verified)
                ja.add(obj)
            }
            json.add("infos", ja)
        }

    }
}