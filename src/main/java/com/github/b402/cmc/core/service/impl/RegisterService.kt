package com.github.b402.cmc.core.service.impl

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.sql.data.UserGender
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.util.md5HashWithSalt
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

object RegisterService : DataService<RegisterData>(
        "register",
        Permission.ANY,
        RegisterData::class.java
) {
    override suspend fun readData(data: RegisterData): ReturnData {
        if (!data.userName.matches("[\\u4e00-\\u9fa5a-zA-Z0-9]{4,12}".toRegex())) {
            return returnData(ILLEGAL_INPUT, "输入的用户名不合法")
        }
        val resp = Channel<String?>()
        val user = User.createUser(data, resp)
        if (user == null) {
            val reason = resp.receive() ?: ""
            return returnData(ERROR,  "创建用户时发生错误: ${reason}")
        }
        user.data["realName"] = data.realName
        user.data["id"] = data.id
        user.data["gender"] = data.gender.name
        user.data["password"] = data.password
        User.syncUser(user.uid)
        val token = Token(user.uid)
        return returnData(SUCCESS) {
            this.json.addProperty("token", token.toTokenString())
        }
    }
}

class RegisterData(json: JsonObject) : SubmitData(json) {
    val userName = json.get("userName").asString!!
    val password = json.get("password").asString!!.md5HashWithSalt()
    val realName = json.get("realName").asString!!
    val id: String? = json.get("id")?.asString
    val gender = UserGender.getGender(json.get("gender").asString!!)
}
