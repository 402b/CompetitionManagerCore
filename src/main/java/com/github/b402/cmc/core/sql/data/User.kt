package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.JudgeType
import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.service.impl.user.RegisterData
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.util.Data
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

enum class UserGender(val key: String) {
    MALE("M"), FEMALE("F");

    companion object {
        fun getGender(key: String): UserGender {
            for (ug in UserGender.values()) {
                if (key == ug.key || key == ug.name) {
                    return ug
                }
            }
            throw IllegalArgumentException("错误的性别")
        }
    }
}

class User(
        val uid: Int,
        val name: String,
        data: String
) {
    val data: ConfigurationSection = MemorySection.readFromJson(data)


    val gender: UserGender
        get() {
            val g = data.getString("gender")!!
            return UserGender.valueOf(g)
        }
    val realName: String
        get() {
            val name = data.getString("realName")!!
            return name
        }
    val id: String
        get() = data.getString("id")!!

    var permission: Permission
        get() {
            val p = Permission.valueOf(data.getString("permission", "User")!!)
            if (!p.contains(Permission.VERIFIED) && this.verified) {
                data["permission"] = Permission.VERIFIED.name
                return Permission.VERIFIED
            }
            return p
        }
        set(value) {
            data["permission"] = value.name
        }

    var verified: Boolean
        get() = data.getBoolean("verified", false)
        set(value) = data.set("verified", value)

    fun checkPassword(password: String): Boolean {
        val pw = data.getString("password")
        return pw == password
    }

    suspend fun getPermission(gid: Int): Deferred<Permission> {
        if (this.permission.contains(Permission.MAIN_JUDGE)) {
            return GlobalScope.async(GlobalScope.coroutineContext) { permission }
        }
        return GlobalScope.async(GlobalScope.coroutineContext) {
            val ji = JudgeInfo.getJudgeInfo(uid, gid, true).await()
            if (ji == null) {
                Permission.VERIFIED
            } else {
                ji.type.permission
            }
        }
    }


    suspend fun sync() = SQLManager.async {
        val user = this@User
        val ps = this.prepareStatement("UPDATE User SET Data = ? WHERE UID = ? LIMIT 1")
        ps.setString(1, user.data.toJson())
        ps.setInt(2, user.uid)
        ps.executeUpdate()
        Token.clearCache(user.uid)
    }

    companion object UserManager {

        suspend fun getUnverifiedUsers() = SQLManager.asyncDeferred {
            val list = mutableListOf<Int>()
            val ps = this.prepareStatement(
                    "SELECT UID FROM User WHERE JSON_EXTRACT(Data,'$.verified') IS NULL OR JSON_EXTRACT(Data,'$.verified') = FALSE"
            )
            val rs = ps.executeQuery()
            while (rs.next()) {
                list += rs.getInt("UID")
            }
            list
        }

        suspend fun createUser(rd: RegisterData): Data<User?, String?> {
            val hasUser = getUserByName(rd.userName)
            var user: User? = null
            var resp: String? = null
            SQLManager.coroutinesConnection {
                if (hasUser.await() == null) {
                    val data = MemorySection()
                    data["realName"] = rd.realName
                    data["id"] = rd.id
                    data["gender"] = rd.gender.name
                    data["password"] = rd.password
                    data["permission"] = Permission.USER.name
                    val insert = this.prepareStatement("INSERT INTO User (Name,Data) VALUES (?,?)")
                    insert.setString(1, rd.userName)
                    insert.setString(2, data.toJson())
                    insert.execute()
                    val sqluser = getUserByName(rd.userName).await()
                    if (sqluser == null) {
                        user = sqluser
                        resp = "数据库异常"
                    } else {
                        user = sqluser
                    }
                } else {
                    user = null
                    resp = "用户名重复"
                }
            }
            return Data(user, resp)
        }


        suspend fun getUser(uid: Int) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM User WHERE UID = ? LIMIT 1")
            ps.setInt(1, uid)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val name = rs.getString("Name")
                val data = rs.getString("Data")
                User(uid, name, data)
            } else {
                null
            }
        }

        suspend fun getUserByName(name: String) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM User WHERE Name = ? LIMIT 1")
            ps.setString(1, name)
            val rs = ps.executeQuery()
            if (rs.next()) {
                val uid = rs.getInt("UID")
                val data = rs.getString("Data")
                User(uid, name, data)
            } else {
                null
            }
        }
    }

    override fun toString(): String {
        return "User(uid=$uid, name='$name', data=$data)"
    }
}