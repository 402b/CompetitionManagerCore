package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.JudgeType
import com.github.b402.cmc.core.sql.SQLManager

data class JudgeInfo(
        val uid: Int,
        val gid: Int,
        var type: JudgeType,
        var verified: Boolean
) {
    suspend fun sync() = SQLManager.async {
        val ps = prepareStatement("UPDATE Judge SET Type = ?, Verified = ? WHERE UID = ? AND GID = ?")
        ps.setString(1, type.name)
        ps.setBoolean(2, verified)
        ps.setInt(3, uid)
        ps.setInt(4, gid)
        ps.executeUpdate()
    }

    companion object {
        suspend fun addJudge(uid: Int, gid: Int, type: JudgeType,verified: Boolean) = SQLManager.async {
            val ps = prepareStatement("INSERT INTO Judge VALUES (?,?,?,?)")
            ps.setInt(1, uid)
            ps.setInt(2, gid)
            ps.setString(3, type.name)
            ps.setBoolean(4, verified)
            ps.executeUpdate()
        }


        suspend fun addJudge(uid: Int, gid: Int, type: JudgeType) = SQLManager.asyncDeferred {
            val ps = prepareStatement("INSERT INTO Judge VALUES (?,?,?,?)")
            ps.setInt(1, uid)
            ps.setInt(2, gid)
            ps.setString(3, type.name)
            ps.setBoolean(4, false)
            ps.executeUpdate()
            getJudgeInfo(uid, gid).await()
        }

        suspend fun removeJudge(uid:Int,gid:Int) = SQLManager.async {
            val ps = this.prepareStatement("DELETE FROM Judge WHERE UID = ? AND GID = ?")
            ps.setInt(1,uid)
            ps.setInt(2,gid)
            ps.executeUpdate()
        }

        suspend fun getJudgeInfo(uid: Int, gid: Int) = SQLManager.asyncDeferred {
            val ps = prepareStatement("SELECT * FROM Judge WHERE UID = ? AND GID = ?")
            ps.setInt(1, uid)
            ps.setInt(2, gid)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val type = JudgeType.valueOf(rs.getString("Type"))
                val verified = rs.getBoolean("Verified")
                JudgeInfo(uid, gid, type, verified)
            } else {
                null
            }
        }

        suspend fun getJudgeInfo(uid: Int, gid: Int, verified: Boolean = true) = SQLManager.asyncDeferred {
            val ps = prepareStatement("SELECT * FROM Judge WHERE UID = ? AND GID = ? AND Verified = ?")
            ps.setInt(1, uid)
            ps.setInt(2, gid)
            ps.setBoolean(3, verified)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val type = JudgeType.valueOf(rs.getString("Type"))
                JudgeInfo(uid, gid, type, verified)
            } else {
                null
            }
        }

        suspend fun getJudgeInfo(uid: Int, verified: Boolean = true) = SQLManager.asyncDeferred {
            val ps = prepareStatement("SELECT * FROM Judge WHERE UID = ? AND Verified = ?")
            ps.setInt(1, uid)
            ps.setBoolean(2, verified)
            val rs = ps.executeQuery()
            val list = mutableListOf<JudgeInfo>()
            while (rs.next()) {
                val gid = rs.getInt("GID")
                val type = JudgeType.valueOf(rs.getString("Type"))
                val verified = rs.getBoolean("Verified")
                list += JudgeInfo(uid, gid, type, verified)
            }
            list
        }

        suspend fun getJudgeInfoByGameID(gid: Int, verified: Boolean = true) = SQLManager.asyncDeferred {
            val ps = prepareStatement("SELECT * FROM Judge WHERE GID = ? AND Verified = ?")
            ps.setInt(1, gid)
            ps.setBoolean(2, verified)
            val rs = ps.executeQuery()
            val list = mutableListOf<JudgeInfo>()
            while (rs.next()) {
                val uid = rs.getInt("UID")
                val type = JudgeType.valueOf(rs.getString("Type"))
                val verified = rs.getBoolean("Verified")
                list += JudgeInfo(uid, gid, type, verified)
            }
            list
        }
    }
}