package com.github.b402.cmc.core.service.impl.score

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Score

object ScoreDataService : DataService<SubmitData>(
        "score_data",
        Permission.ANY,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val uid = data.json.getInt("uid")
        val gid = data.json.getInt("gid")
        val score = Score.getScore(uid, gid).await() ?: return returnData(NO_DATA, "没有数据")
        return returnData(SUCCESS) {
            json.addProperty("uid", uid)
            json.addProperty("gid", gid)
            json.addProperty("score", score.score)
            json.addProperty("submitJudge", score.submitJudge)
            json.addProperty("verified", score.verified)
//            json.addProperty("verifiedJudge", score.verifiedJudge ?: -1)
        }
    }

}