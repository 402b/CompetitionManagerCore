package com.github.b402.cmc.core.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SubmitData
import com.google.gson.JsonObject

class RegisterService : DataService<RegisterData>(
        "register",
        Permission.ANY,
        RegisterData::class.java
) {
    override fun readData(data: RegisterData): ReturnData {
        TODO()
    }
}

class RegisterData(json: JsonObject) : SubmitData(json) {
    val userName = json.get("userName").asString
    val password = json.get("password").asString
}

class RegisterResultData(status: String) : ReturnData(status) {

}