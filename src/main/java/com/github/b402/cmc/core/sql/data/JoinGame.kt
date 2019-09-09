package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.sql.SQLManager

object JoinGame {
    suspend fun hasJoinedGame(uid:Int, gid:Int, archive: Boolean = false) = SQLManager.asyncDeferred {
        val ps = this.prepareStatement("SELECT GID FROM JoinGame,Game WHERE UID = ? AND JoinGame.GID = Game.ID AND JoinGame.GID = ? AND GAME.Archive = ?")
        ps.setInt(1, uid)
        ps.setInt(2, gid)
        ps.setBoolean(3, archive)
        val rs = ps.executeQuery()
        rs.next()
    }

    suspend fun getJoinedGame(uid: Int, archive: Boolean = false) = SQLManager.asyncDeferred {
        val ps = this.prepareStatement("SELECT GID FROM JoinGame,Game WHERE UID = ? AND JoinGame.GID = Game.ID AND GAME.Archive = ?")
        ps.setInt(1, uid)
        ps.setBoolean(2, archive)
        val rs = ps.executeQuery()
        val list = mutableListOf<Int>()
        while (rs.next()) {
            list += rs.getInt(1)
        }
        list
    }

    suspend fun getJoinedInfo(gid:Int,archive: Boolean= false) = SQLManager.asyncDeferred{
        val ps = this.prepareStatement("SELECT UID FROM JoinGame,Game WHERE JoinGame.GID = ? AND JoinGame.GID = Game.ID AND GAME.Archive = ?")
        ps.setInt(1, gid)
        ps.setBoolean(2, archive)
        val rs = ps.executeQuery()
        val list = mutableListOf<Int>()
        while(rs.next()){
            val uid = rs.getInt("UID")
            list += uid
        }
        list
    }
}