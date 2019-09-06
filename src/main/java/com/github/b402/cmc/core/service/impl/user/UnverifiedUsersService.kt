package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SUCCESS
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.sql.data.User
import com.google.gson.JsonArray

object UnverifiedUsersService : DataService<SubmitData>(
        "admin_unverifiedUsers",
        Permission.ADMIN,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val users = User.getUnverifiedUsers().await()
                ?: return returnData("ERROR", "数据库异常")
        val ja = JsonArray()
        for (id in users) {
            ja.add(id)
        }
        return returnData(SUCCESS) {
            this.json.add("users", ja)
        }
    }
}