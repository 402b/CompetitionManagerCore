package com.github.b402.cmc.core.service.impl

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.sql.data.User

object UserInfoService : DataService<SubmitData>(
        "userinfo",
        Permission.USER,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val token = data.token!!
        val user = token.getUser()
//        val ru = User.getUser(token.uid)
//        val user = ru.await() ?: return returnData(ERROR, "找不到用户数据 uid:${token.uid}")
        return returnData(SUCCESS, token) {
            json.addProperty("realName", user.realName)
            json.addProperty("gender", user.gender.key)
            json.addProperty("uid", user.uid)
            json.addProperty("id", user.id)
        }
    }
}