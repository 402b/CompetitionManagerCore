package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.event.EventBus
import com.github.b402.cmc.core.event.game.GameCompleteEvent
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.sort.Sort
import com.github.b402.cmc.core.sort.SortCache
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.Score
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object GameCompleteService : DataService<SubmitData>(
        "game_complete",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val gid = data.json.getInt("gid")
        if (data.getUser()?.getPermission(gid)?.await()?.contains(Permission.PROJECT_JUDGE) != true) {
            return returnData(ILLEGAL_PERMISSION, "权限不足")
        }
        val top = data.json.getInt("top")
        val dscores = Score.getScoreByGame(gid)
        val game = Game.getGame(gid).await() ?: return returnData(ERROR, "找不到比赛")
        if (game.archive || game.complete) {
            return returnData(ERROR, "比赛已经结束了")
        }
        val sort = Sort.getSort(game.sortType) ?: return returnData(ERROR, "找不到排序方式")
        val result = mutableListOf<Pair<Int, SortCache<SortCache<*>>>>()
        val scores = mutableMapOf<Int, Score>()
        for (score in dscores.await() ?: return returnData(ERROR, "数据库异常")) {
            result += (score.uid to sort.getScore(score.score)!! as SortCache<SortCache<*>>)
            scores[score.uid] = score
        }
        result.sortWith(Comparator { a, b ->
            a.second.compareTo(b.second)
        })
        val toplist = result.subList(0, top)
        val array = JsonArray()
        var rank = 1
        for (r in toplist) {
            val obj = JsonObject()
            val score = scores[r.first]!!
            obj.addProperty("uid", r.first)
            obj.addProperty("rank", rank)
            obj.addProperty("score", score.score)
            array.add(obj)
            rank++
        }
        val obj = JsonObject()
        obj.add("result",array)
        Game.createResult(Configuration.gson.toJson(obj),gid).await()
        val gce = GameCompleteEvent(game)
        EventBus.callEvent(gce)
        return returnData(SUCCESS)
    }
}