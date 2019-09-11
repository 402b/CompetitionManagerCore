package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.user.UserJoinGameEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.JoinGame

object GameJoinService : DataService<SubmitData>(
        "game_join",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        val cancel = data.json.getBoolean("cancel", false)
        val dgame = Game.getGame(gid)
        val djoined = JoinGame.hasJoinedGame(data.token!!.uid, gid)
        val game = dgame.await() ?: return returnData(ERROR, "找不到比赛")
        if (System.currentTimeMillis() > game.endTime || System.currentTimeMillis() < game.startTime) {
            return returnData(ERROR, "未到比赛报名时间或比赛已经停止报名")
        }
        val ujge = UserJoinGameEvent(gid, data.token!!.uid, cancel)
        EventBus.callEvent(ujge)
        if (ujge.isCancel) {
            return returnData(DENY, ujge.reason ?: "")
        }
        if (cancel) {
            if (!(djoined.await() ?: return returnData(ERROR, "数据库异常"))) {
                return returnData(ILLEGAL_STATE, "未过比赛")
            }
            JoinGame.removeJoin(data.token!!.uid, gid).await()
            return returnData(SUCCESS)
        }
        if (djoined.await() ?: return returnData(ERROR, "数据库异常")) {
            return returnData(ILLEGAL_STATE, "已经加入过比赛")
        }
        if (JoinGame.joinGame(data.token!!.uid, gid).await()) {
            return returnData(SUCCESS)
        } else {
            return returnData(ERROR, "数据库异常")
        }
    }
}