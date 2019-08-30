package com.github.b402.cmc.core.service.impl

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.util.md5HashWithSalt
import com.google.gson.JsonObject

object LoginService : DataService<LoginData>(
        "login",
        Permission.ANY,
        LoginData::class.java
) {
    override suspend fun readData(data: LoginData): ReturnData {
        val cuser = User.getUserByName(data.userName)
        val user = cuser.receive() ?: return returnData(INVALID_USER_OR_PASSWORD, "错误的用户名或密码")
        return if (user.checkPassword(data.password)) {
            val token = Token(user.uid)
            returnData(SUCCESS) {
                this.json.addProperty("token", token.toTokenString())
            }
        } else {
            returnData(INVALID_USER_OR_PASSWORD, "错误的用户名或密码")
        }
    }
}

class LoginData(json: JsonObject) : SubmitData(json) {
    val userName = json.get("userName").asString!!
    val password = json.get("password").asString!!.md5HashWithSalt()
}