package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.sql.SQLManager

data class Score(
        val uid: Int,
        val gid: Int,
        var score: String,
        var submitJudge: Int,
        var verified: Boolean = false,
        var verifiedJudge: Int? = null
) {

    suspend fun sync(
            resetVerified: Boolean = false
    ) = SQLManager.async {
        val ps = this.prepareStatement("UPDATE Score SET Score = ?, SubmitJudge = ?, Verified = ?, VerifiedJudge = ? WHERE UID = ? AND GID = ? LIMIT 1")
        ps.setString(1, score)
        ps.setInt(2, submitJudge)
        if (resetVerified) {
            ps.setBoolean(3, false)
            ps.setObject(4, null)
        } else {
            ps.setBoolean(3, verified)
            if (verifiedJudge == null) {
                ps.setObject(4, null)
            } else {
                ps.setInt(4, verifiedJudge!!)
            }
        }
        ps.setInt(5, uid)
        ps.setInt(6, gid)
        ps.executeUpdate()
    }

    suspend fun create() = SQLManager.async {
        val ps = this.prepareStatement("INSERT INTO Score (UID, GID, Score, SubmitJudge) VALUES (?, ?, ?, ?)")
        ps.setInt(1, uid)
        ps.setInt(2, gid)
        ps.setString(3, score)
        ps.setInt(4, submitJudge)
        ps.executeUpdate()
    }

    companion object {
        suspend fun getScore(uid: Int, gid: Int) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM Score WHERE UID = ? AND GID = ? LIMIT 1")
            ps.setInt(1, uid)
            ps.setInt(2, gid)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val score = rs.getString("Score")
                val submitJudge = rs.getInt("SubmitJudge")
                val verified = rs.getBoolean("Verified")
                var verifiedJudge: Int? = rs.getInt("VerifiedJudge")
                if (verifiedJudge == 0) {
                    verifiedJudge = null
                }
                Score(uid, gid, score, submitJudge, verified, verifiedJudge)
            } else {
                null
            }
        }

        suspend fun getScoreByGame(gid: Int) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM Score WHERE GID = ?")
            ps.setInt(1, gid)
            val rs = ps.executeQuery()
            val list = mutableListOf<Score>()
            while (rs.next()) {
                val uid = rs.getInt("UID")
                val score = rs.getString("Score")
                val submitJudge = rs.getInt("SubmitJudge")
                val verified = rs.getBoolean("Verified")
                var verifiedJudge: Int? = rs.getInt("VerifiedJudge")
                if (verifiedJudge == 0) {
                    verifiedJudge = null
                }
                list += Score(uid, gid, score, submitJudge, verified, verifiedJudge)
            }
            list
        }

        suspend fun getScoreByUser(uid: Int) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM Score WHERE uID = ?")
            ps.setInt(1, uid)
            val rs = ps.executeQuery()
            val list = mutableListOf<Score>()
            while (rs.next()) {
                val gid = rs.getInt("GID")
                val score = rs.getString("Score")
                val submitJudge = rs.getInt("SubmitJudge")
                val verified = rs.getBoolean("Verified")
                var verifiedJudge: Int? = rs.getInt("VerifiedJudge")
                if (verifiedJudge == 0) {
                    verifiedJudge = null
                }
                list += Score(uid, gid, score, submitJudge, verified, verifiedJudge)
            }
            list
        }
    }
}