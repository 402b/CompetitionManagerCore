package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.sql.SQLManager

object JoinGame {
    suspend fun joinGame(uid: Int, gid: Int) = SQLManager.async {
        val ps = this.prepareStatement("INSERT INTO JoinGame (UID,GID,Time) VALUES (?,?,?)")
        ps.setInt(1, uid)
        ps.setInt(2, gid)
        ps.setLong(3, System.currentTimeMillis())
        ps.executeUpdate()
    }

    suspend fun verifyJoin(uid:Int,gid:Int,verified: Boolean) = SQLManager.async {
        val ps = this.prepareStatement("UPDATE JoinGame SET Verified = ? WHERE UID = ? AND GID = ?")
        ps.setBoolean(1,verified)
        ps.setInt(2,uid)
        ps.setInt(3,gid)
        ps.executeUpdate()
    }

    suspend fun removeJoin(uid: Int, gid: Int) = SQLManager.async {
        val ps = this.prepareStatement("DELETE FROM JoinGame WHERE UID = ? AND GID = ?")
        ps.setInt(1, uid)
        ps.setInt(2, gid)
        ps.executeUpdate()
    }

    suspend fun hasJoinedGame(uid: Int, gid: Int, archive: Boolean = false) = SQLManager.asyncDeferred {
        val ps = this.prepareStatement("SELECT GID FROM JoinGame,Game WHERE UID = ? AND JoinGame.GID = Game.ID AND JoinGame.GID = ? AND GAME.Archive = ?")
        ps.setInt(1, uid)
        ps.setInt(2, gid)
        ps.setBoolean(3, archive)
        val rs = ps.executeQuery()
        rs.next()
    }

    /**
     * @return 返回这个用户加入的比赛列表
     */
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

    /**
     * @return 返回加入这个比赛的用户列表
     */
    suspend fun getJoinedInfo(gid: Int, archive: Boolean = false, verified: Boolean = true) = SQLManager.asyncDeferred {
        val ps = this.prepareStatement("SELECT UID FROM JoinGame,Game WHERE JoinGame.GID = ? AND JoinGame.GID = Game.ID AND GAME.Archive = ? AND Verified = ?")
        ps.setInt(1, gid)
        ps.setBoolean(2, archive)
        ps.setBoolean(3, verified)
        val rs = ps.executeQuery()
        val list = mutableListOf<Int>()
        while (rs.next()) {
            val uid = rs.getInt("UID")
            list += uid
        }
        list
    }
}