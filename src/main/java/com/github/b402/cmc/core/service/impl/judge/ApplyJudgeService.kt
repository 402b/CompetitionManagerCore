package com.github.b402.cmc.core.service.impl.judge

import com.github.b402.cmc.core.JudgeType
import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserJudgeApplyEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.JudgeInfo

object ApplyJudgeService : DataService<SubmitData>(
        "judge_apply",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val defji = JudgeInfo.getJudgeInfo(data.token!!.uid, gid)
        val game = Game.getGame(gid).await()
        if (game == null || game.archive) {
            return returnData(ILLEGAL_INPUT, "找不到比赛或比赛已失效")
        }
        val jinfo = defji.await()
        if (jinfo != null) {
            return returnData(ILLEGAL_INPUT, "你已经报名过这个比赛的裁判了")
        }
        val addJudge = JudgeInfo.addJudge(data.token!!.uid, gid, JudgeType.NORMAL).await()
        if (addJudge != null) {
            val ujae = UserJudgeApplyEvent(data.token!!.uid,gid)
            EventBus.callEvent(ujae)
            return returnData(SUCCESS)
        } else {
            return returnData(ERROR, "数据库异常")
        }
    }
}