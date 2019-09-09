package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.sql.data.User
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.min

object UserInfoService : DataService<SubmitData>(
        "user_info",
        Permission.USER,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val token = data.token!!
        val user = token.getUser()
        if (data.json.contains("uid")) {
            if (!user.permission.contains(Permission.JUDGE)) {
                return returnData(ILLEGAL_PERMISSION, "权限不足")
            }
            val array = JsonArray()
            val jobs = mutableListOf<Job>()
            val ids = data.json.getNumberList("uid")!!
            for (i in 0 until min(ids.size, 10)) {
                val id = ids[i].toInt()
                jobs += GlobalScope.launch(coroutineContext) {
                    val user = User.getUser(id).await()
                    val json = JsonObject()
                    json.addProperty("uid", id)
                    if (user == null) {
                        json.addProperty("status", ERROR)
                        json.addProperty("reason", "找不到用户")
                    } else {
                        json.addProperty("status", SUCCESS)
                        json.addProperty("realName", user.realName)
                        json.addProperty("gender", user.gender.key)
                        json.addProperty("uid", user.uid)
                        json.addProperty("id", user.id)
                        json.addProperty("permission", user.permission.name)
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
        }
        return returnData(SUCCESS, token) {
            json.addProperty("realName", user.realName)
            json.addProperty("gender", user.gender.key)
            json.addProperty("uid", user.uid)
            json.addProperty("id", user.id)
            json.addProperty("permission", user.permission.name)
        }
    }
}