package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserVerifyJudgeEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JudgeInfo
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object VerifyJudgeService : DataService<SubmitData>(
        "judge_verify",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val user = data.token?.getUser()
        if (user == null || !user.getPermission(gid).await().contains(Permission.PROJECT_JUDGE)) {
            return returnData(ERROR, "权限不足")
        }
        val verify = data.json.getBoolean("verify", false)
        val uids = data.json.getNumberList("checked") ?: return returnData(ILLEGAL_INPUT, "参数错误")
        val result = JsonArray()
        val jobs = mutableListOf<Job>()
        for (id in uids) {
            jobs += GlobalScope.launch(GlobalScope.coroutineContext) {
                val obj = JsonObject()
                val uid = id.toInt()
                obj.addProperty("uid", uid)
                val info = JudgeInfo.getJudgeInfo(uid, gid,!verify).await()
                if (info == null) {
                    obj.addProperty("status", ERROR)
                    obj.addProperty("reason", "找不到用户的申请信息")
                } else {
                    info.verified = verify
                    if(info.sync().await()){
                        if(!verify){
                            JudgeInfo.removeJudge(uid,gid).await()
                        }
                        val uvje = UserVerifyJudgeEvent(uid,gid,user.uid,verify)
                        EventBus.callEvent(uvje)
                        obj.addProperty("status", SUCCESS)
                    }else{
                        obj.addProperty("status", ERROR)
                        obj.addProperty("reason", "数据库异常")
                    }
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