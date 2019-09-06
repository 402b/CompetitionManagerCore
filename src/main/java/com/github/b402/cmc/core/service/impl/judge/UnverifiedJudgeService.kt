package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JudgeInfo
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object UnverifiedJudgeService : DataService<SubmitData>(
        "judge_unverified",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val user = data.getUser() ?: return returnData(ERROR, "权限不足")
        val permission = user.getPermission(gid).await()
        if (!permission.contains(Permission.PROJECT_JUDGE)) {
            return returnData(ERROR, "权限不足")
        }
        val infos = JudgeInfo.getJudgeInfoByGameID(gid, false).await()
                ?: return returnData(ERROR, "数据库异常")
        return returnData(SUCCESS) {
            val arr = JsonArray()
            for (info in infos) {
                arr.add(info.uid)
            }
            json.add("uids", arr)
        }
    }
}