package com.github.b402.cmc.core.service.impl.score

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.JoinGame
import com.github.b402.cmc.core.sql.data.JudgeInfo
import com.github.b402.cmc.core.sql.data.Score

object UploadScoreService : DataService<SubmitData>(
        "score_upload",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val permission = data.token!!.getUser().getPermission(gid).await()
        if (!permission.contains(Permission.JUDGE)) {
            return returnData(ILLEGAL_PERMISSION, "权限不足")
        }
        val dgame = Game.getGame(gid)
        var update = data.json.getBoolean("update", false)
        val uid = data.json.getInt("uid")
        val scoreStr = data.json.getString("score") ?: return returnData(ERROR, "参数不足")
        val dscore = Score.getScore(uid, gid)
        val djoined = JoinGame.hasJoinedGame(uid, gid, false)
        if (dscore.await() != null && !update) {
            return returnData(ERROR, "已经提交过成绩")
        }
        if (djoined.await() != true) {
            return returnData(ERROR, "此用户未参加过该比赛")
        }
        val game = dgame.await() ?: return returnData(ERROR, "找不到比赛")
        if (game.closeUpload) {
            return returnData(ILLEGAL_STATE, "该比赛已停止成绩上传")
        }
        val score: Score// = Score(uid,gid,scoreStr,data.token!!.uid)
        if (update) {
            val tmp = dscore.await()
            if (tmp == null) {
                score = Score(uid, gid, scoreStr, data.token!!.uid)
                update = false
            } else {
                score = tmp
                score.score = scoreStr
                score.submitJudge = data.token!!.uid
            }
        } else {
            score = Score(uid, gid, scoreStr, data.token!!.uid)
        }
        if (update) {
            if (score.sync(false).await()) {
                return returnData(SUCCESS)
            }
        } else {
            if (score.create().await()) {
                return returnData(SUCCESS)
            }
        }
        return returnData(ERROR, "数据库异常")
    }
}