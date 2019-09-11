package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserVerifyEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.User
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object VerifyUserService : DataService<SubmitData>(
        "admin_verifyUser",
        Permission.ADMIN,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val ids = data.json.getNumberList("checked") ?: return returnData("ERROR", "传入参数过少")
        val verify = data.json.getBoolean("isAgree", false)
        val jobs = mutableListOf<Job>()
        val result = JsonArray()
        for (id in ids) {
            val id = id.toInt()
            val du = User.getUser(id)
            jobs += GlobalScope.launch(GlobalScope.coroutineContext) {
                val user = du.await()
                val obj = JsonObject()
                obj.addProperty("uid", id)
                if (user == null) {
                    obj.addProperty("status", ERROR)
                    obj.addProperty("reason", "找不到用户的信息")
                } else {
                    user.verified = verify
                    if (user.sync().await()) {
                        val uve = UserVerifyEvent(id,data.token!!.uid,verify)
                        EventBus.callEvent(uve)
                        obj.addProperty("status", SUCCESS)
                    } else {
                        obj.addProperty("status", ERROR)
                        obj.addProperty("reason", "数据库异常")
                    }
                }
            }
        }
        for (job in jobs) {
            job.join()
        }
        return returnData(SUCCESS) {
            this.json.add("result", result)
        }
    }

}