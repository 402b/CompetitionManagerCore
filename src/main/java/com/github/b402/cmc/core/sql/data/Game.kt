package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.service.impl.game.CreateGameData
import com.github.b402.cmc.core.sort.Sort
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.util.Data
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap


class Game(
        val id: Int,
        val name: String,
        val archive: Boolean,
        data: String
) {
    val data: ConfigurationSection = MemorySection.readFromJson(data)
    val amount: Int
        get() = data.getInt("amount")
    val time: Long
        get() = data.getLong("time")
    val startTime: Long
        get() = data.getLong("startTime")
    val endTime: Long
        get() = data.getLong("endTime")
    var closeUpload: Boolean
        get() = data.getBoolean("closeUpload", false)
        set(value) = data.set("closeUpload", value)

    val sortType: String
        get() = data.getString("sortType")!!

    var complete: Boolean
        get() = data.getBoolean("complete", false)
        set(value) = data.set("complete", value)


    suspend fun sync() = SQLManager.async {
        val ps = this.prepareStatement("UPDATE Game SET Data = ? WHERE ID = ? LIMIT 1")
        ps.setString(1, data.toJson())
        ps.setInt(2, id)
        ps.executeUpdate()
    }

    override fun toString(): String {
        return "Game(id=$id, name='$name', data=${data.toJson()})"
    }

    companion object GameManager {
        val cacheGame: MutableMap<Int, Game> = ConcurrentHashMap()

        suspend fun getAllGames(archive: Boolean) = SQLManager.asyncDeferred {
            val games = mutableListOf<Int>()
            val ps = this.prepareStatement("SELECT ID FROM Game WHERE Archive = ?")
            ps.setBoolean(1, archive)
            val rs = ps.executeQuery()
            while (rs.next()) {
                games += rs.getInt("ID")
            }
            games
        }

        suspend fun createGame(data: CreateGameData): Data<Game?, String?> {
            val dg = getGameByName(data.name)
            if (dg.await() != null) {
                return Data(null, "已有同名比赛")
            }
            SQLManager.coroutinesConnection {
                val ms = MemorySection()
                ms["amount"] = data.amount
                ms["time"] = data.time
                ms["startTime"] = data.startTime
                ms["endTime"] = data.endTime
                ms["sortType"] = data.sortType
                val ps = this.prepareStatement("INSERT INTO Game (Name,Data) VALUES (?,?)")
                ps.setString(1, data.name)
                ps.setString(2, ms.toJson())
                ps.executeUpdate()
            }
            val game = getGameByName(data.name).await() ?: return Data(null, "数据库异常")
            return Data(game, null)
        }

        suspend fun getGameByName(name: String) = SQLManager.asyncDeferred<Game> {
            val ps = this.prepareStatement("SELECT * FROM Game WHERE Name = ? LIMIT 1")
            ps.setString(1, name)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val id = rs.getInt("ID")
                val data = rs.getString("Data")
                val archive = rs.getBoolean("Archive")
                Game(id, name, archive, data)
            } else {
                null
            }
        }

        suspend fun getGame(id: Int) = SQLManager.asyncDeferred {
            val ps = prepareStatement("SELECT * FROM Game WHERE ID = ? LIMIT 1")
            ps.setInt(1, id)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val name = rs.getString("Name")
                val data = rs.getString("Data")
                val archive = rs.getBoolean("Archive")
                Game(id, name, archive, data)
            } else {
                null
            }
        }

        suspend fun getGameResult(gid: Int) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM GameResult WHERE GID = ? LIMIT 1")
            ps.setInt(1, gid)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val result = rs.getString("Result")!!
                val time = rs.getLong("Time")
                return@asyncDeferred GameResult(gid, result, time)
            } else {
                null
            }
        }

        suspend fun getAllGameResult() = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM GameResult")
            val rs = ps.executeQuery()
            val list = mutableListOf<GameResult>()
            while (rs.next()) {
                val result = rs.getString("Result")!!
                val time = rs.getLong("Time")
                val gid = rs.getInt("GID")
                list += GameResult(gid, result, time)
            }
            list
        }

        suspend fun createResult(result: String, gid: Int) = SQLManager.async {
            val ps = this.prepareStatement("INSERT INTO GameResult VALUES (?,?,?)")
            ps.setInt(1, gid)
            ps.setString(2, result)
            ps.setLong(3, System.currentTimeMillis())
            ps.executeUpdate()
        }
    }


}

data class GameResult(
        val gid: Int,
        val result: String,
        val time: Long
){
    val resultJson:JsonElement by lazy {
        Configuration.parser.parse(result)
    }
}