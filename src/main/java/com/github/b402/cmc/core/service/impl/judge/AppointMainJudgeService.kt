package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.User

object AppointMainJudgeService : DataService<SubmitData>(
        "admin_appointMainJudge",
        Permission.ADMIN,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val uid = data.json.getInt("uid")
        val appoint = data.json.getBoolean("appoint", false)
        val user = User.getUser(uid).await() ?: return returnData(ERROR, "找不到用户")
        if (!appoint && user.permission != Permission.MAIN_JUDGE) {
            return returnData(ERROR, "该用户本身就不是主裁判")
        }else if(appoint && !user.verified){
            return returnData(ERROR, "该用户尚未认证")
        }else if(appoint && user.permission == Permission.MAIN_JUDGE){
            return returnData(ERROR, "该用户本身就是主裁判")
        }
        user.permission = if (appoint) Permission.MAIN_JUDGE else Permission.VERIFIED
        if (user.sync().await()) {
            return returnData(SUCCESS)
        } else {
            return returnData(ERROR, "数据库异常")
        }
    }
}