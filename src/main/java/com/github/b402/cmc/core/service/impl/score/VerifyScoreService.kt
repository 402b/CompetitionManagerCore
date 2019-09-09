package com.github.b402.cmc.core.service.impl.score

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Score
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

object VerifyScoreService:DataService<SubmitData>(
        "score_verify",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val checked = data.json.getNumberList("checked") ?: return returnData(ILLEGAL_INPUT,"传入参数不足")
        val verify = data.json.getBoolean("verify")
        val jobs = mutableListOf<Job>()
        val vuid = data.token!!.uid
        for(num in checked){
            val id = num.toInt()
            jobs += GlobalScope.launch(coroutineContext) {
                val score = Score.getScore(id, gid).await() ?: return@launch
                if(verify){
                    score.verified = true
                    score.verifiedJudge = vuid
                    score.sync().await()
                }else{
                    score.remove().await()
                }
            }
        }
        for(job in jobs){
            job.join()
        }
        return returnData(SUCCESS)
    }
}