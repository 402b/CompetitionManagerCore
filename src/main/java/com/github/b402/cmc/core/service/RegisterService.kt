package com.github.b402.cmc.core.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SUCCESS
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.coroutines.Continuation

class RegisterService : DataService<RegisterData>(
        "register",
        Permission.ANY,
        RegisterData::class.java
) {
    override suspend fun readData(data: RegisterData, cs: CoroutineScope): ReturnData {

        return returnData(SUCCESS) {
            this.json.addProperty("user", data.userName)
            this.json.addProperty("password", data.password)
        }
    }
}

class RegisterData(json: JsonObject) : SubmitData(json) {
    val userName = json.get("userName").asString
    val password = json.get("password").asString
}
