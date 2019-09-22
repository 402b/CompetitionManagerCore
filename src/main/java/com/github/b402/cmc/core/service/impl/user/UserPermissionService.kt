package com.github.b402.cmc.core.service.impl.user

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SUCCESS
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.sql.data.JudgeInfo

object UserPermissionService : DataService<SubmitData>(
        "user_permission",
        Permission.USER,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val user = data.getUser()!!
        var result = user.permission
        val permissions = JudgeInfo.getJudgeInfo(user.uid, true).await()!!
        for (per in permissions) {
            if (per.type.permission.contains(result)) {
                result = per.type.permission
            }
        }
        return returnData(SUCCESS) {
            json.addProperty("permission", result.name)
        }
    }
}