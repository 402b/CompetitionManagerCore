package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.service.impl.game.CreateGameData
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.util.Data
import java.util.concurrent.ConcurrentHashMap

enum class GameType(val key: String) {
    PREGAME("预赛"),
    PREFINAL("预决赛"),
    FINAL("决赛"),
    SEMIFINAL("半决赛");

    companion object {
        fun getGameType(key: String): GameType {
            for (ug in GameType.values()) {
                if (key == ug.key || key == ug.name) {
                    return ug
                }
            }
            throw IllegalArgumentException("错误的比赛类型")
        }
    }
}

class Game(
        val id: Int,
        val name: String,
        val type: GameType,
        val archive:Boolean,
        data: String
) {
    val data: ConfigurationSection = MemorySection.readFromJson(data)
    val amount: Int
        get() = data.getInt("amount")
    val time: Long
        get() = data.getLong("time")
    val startTime:Long
        get() = data.getLong("startTime")
    val endTime:Long
        get() = data.getLong("endTime")

    suspend fun sync() = SQLManager.async {
        val ps = this.prepareStatement("UPDATE Game SET Data = ? WHERE ID = ? LIMIT 1")
        ps.setString(1, data.toJson())
        ps.setInt(2, id)
        ps.executeUpdate()
    }

    override fun toString(): String {
        return "Game(id=$id, name='$name', type=$type, data=${data.toJson()})"
    }

    companion object GameManager {
        val cacheGame: MutableMap<Int, Game> = ConcurrentHashMap()

        suspend fun getAllGames(archive:Boolean) = SQLManager.asyncDeferred {
            val games = mutableListOf<Int>()
            val ps = this.prepareStatement("SELECT ID FROM Game WHERE Archive = ?")
            ps.setBoolean(1,archive)
            val rs = ps.executeQuery()
            while(rs.next()){
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
                val ps = this.prepareStatement("INSERT INTO Game (Name,Type,Data) VALUES (?,?,?)")
                ps.setString(1, data.name)
                ps.setString(2, data.type.name)
                ps.setString(3, ms.toJson())
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
                val type = GameType.valueOf(rs.getString("Type"))
                val data = rs.getString("Data")
                val archive = rs.getBoolean("Archive")
                Game(id, name, type,archive, data)
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
                val type = GameType.valueOf(rs.getString("Type"))
                val data = rs.getString("Data")
                val archive = rs.getBoolean("Archive")
                Game(id, name, type,archive, data)
            } else {
                null
            }
        }
    }


}