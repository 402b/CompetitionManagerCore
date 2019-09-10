package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.JudgeType
import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.JudgeInfo
import com.github.b402.cmc.core.sql.data.User

object AppointProjectJudgeService : DataService<SubmitData>(
        "admin_appointProjectJudge",
        Permission.ADMIN,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val uid = data.json.getInt("uid")
        val gid = data.json.getInt("gid")
        val appoint = data.json.getBoolean("appoint", false)
        val user = User.getUser(uid).await() ?: return returnData(ERROR, "找不到用户")
        val permission = user.getPermission(gid).await()
        if (!appoint && permission != Permission.PROJECT_JUDGE) {
            return returnData(ERROR, "该用户本身就不是项目裁判")
        } else if (appoint && !user.verified) {
            return returnData(ERROR, "该用户尚未认证")
        } else if (appoint && permission == Permission.PROJECT_JUDGE) {
            return returnData(ERROR, "该用户本身就是项目裁判")
        }
        JudgeInfo.removeJudge(uid,gid).await()
        if (appoint){
            JudgeInfo.addJudge(uid, gid, JudgeType.PROJECT,true).await()
        }
        return returnData(SUCCESS)
    }
}