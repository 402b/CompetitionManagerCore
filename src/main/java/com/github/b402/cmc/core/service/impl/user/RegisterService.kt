package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserRegisterEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.sql.data.UserGender
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.util.hashSHA256WithSalt
import com.github.b402.cmc.core.util.toBase64
import com.google.gson.JsonObject

object RegisterService : DataService<RegisterData>(
        "user_register",
        Permission.ANY,
        RegisterData::class.java
) {
    override suspend fun onRequest(data: RegisterData): ReturnData {
        if (!data.userName.matches("[\\u4e00-\\u9fa5a-zA-Z0-9]{4,12}".toRegex())) {
            return returnData(ILLEGAL_INPUT, "输入的用户名不合法")
        }
        val (user, resp) = User.createUser(data)
        if (user == null) {
            val reason = resp ?: ""
            return returnData(ERROR, "创建用户时发生错误: ${reason}")
        }
        val token = Token(user.uid)
        val rue = UserRegisterEvent(user.uid, data.id ?: "", data.gender, data.userName)
        EventBus.callEvent(rue)
        if (rue.isCancel) {
            return returnData(DENY, rue.reason ?: "")

        }
        return returnData(SUCCESS) {
            this.json.addProperty("token", token.toTokenString())
        }
    }
}

class RegisterData(json: JsonObject) : SubmitData(json) {
    val userName = json.get("userName").asString!!
    val password = json.get("password").asString!!.hashSHA256WithSalt(userName.toBase64())
    val realName = json.get("realName").asString!!
    val id: String? = json.get("id")?.asString
    val gender = UserGender.getGender(json.get("gender").asString!!)
}
