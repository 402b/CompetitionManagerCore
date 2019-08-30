package com.github.b402.cmc.core.sql.data

import com.github.b402.cmc.core.configuration.ConfigurationSection
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.service.impl.RegisterData
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.util.asyncSend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext

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
        val name: String
) {
    lateinit var data: ConfigurationSection

    constructor(uid: Int, name: String, data: String) : this(uid, name) {
        this.data = MemorySection.readFromJson(data)
    }

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
    val id:String
        get() =  data.getString("id")!!


    fun checkPassword(password:String):Boolean{
        val pw = data.getString("password")
        return pw == password
    }

    companion object UserManager {
        val cacheUser: MutableMap<Int, User> = ConcurrentHashMap()

        fun syncUser(uid: Int) {
            GlobalScope.launch {
                val user = cacheUser[uid] ?: return@launch
                SQLManager.coroutinesConnection {
                    val ps = this.prepareStatement("UPDATE User SET Data = ? WHERE UID = ? LIMIT 1")
                    ps.setString(1, user.data.toJson())
                    ps.setInt(2, uid)
                    ps.executeUpdate()
                }
            }
        }

        suspend fun createUser(rd: RegisterData, resp: Channel<String?>): User? {
            val hasUser = getUserByName(rd.userName)
            val uid = Channel<User?>()
            SQLManager.coroutinesConnection {
                if (hasUser.receive() == null) {
                    val insert = this.prepareStatement("INSERT INTO User (Name,Data) VALUES (?,?)")
                    insert.setString(1, rd.userName)
                    insert.setString(2, "{}")
                    insert.execute()
                    val sqluser = getUserByName(rd.userName).receive()
                    if (sqluser == null) {
                        uid.asyncSend(null)
                        resp.asyncSend("数据库异常")
                    } else {
                        uid.asyncSend(sqluser)
                        cacheUser[sqluser.uid] = sqluser
                        resp.asyncSend(null)
                    }
                } else {
                    uid.asyncSend(null)
                    resp.asyncSend("用户名重复")
                }
            }
            return uid.receive()
        }

        suspend fun getUser(uid: Int): Channel<User?> {
            val channel = Channel<User?>()
            GlobalScope.launch(coroutineContext) {
                val cache = cacheUser[uid]
                if (cache != null) {
                    channel.asyncSend(cache)
                    return@launch
                }
                SQLManager.coroutinesConnection {
                    val ps = this.prepareStatement("SELECT * FROM User WHERE UID = ? LIMIT 1")
                    ps.setInt(1, uid)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        val name = rs.getString("Name")
                        val data = rs.getString("Data")
                        val u = User(uid, name, data)
                        channel.asyncSend(u)
                    } else {
                        channel.asyncSend(null)
                    }
                }
            }
            return channel
        }

        suspend fun getUserByName(name: String): Channel<User?> {
            val channel = Channel<User?>()
            GlobalScope.launch(coroutineContext) {
                SQLManager.coroutinesConnection {
                    val ps = this.prepareStatement("SELECT * FROM User WHERE Name = ? LIMIT 1")
                    ps.setString(1, name)
                    val rs = ps.executeQuery()
                    if (rs.next()) {
                        val uid = rs.getInt("UID")
                        val data = rs.getString("Data")
                        val u = User(uid, name, data)
                        channel.asyncSend(u)
                        cacheUser[u.uid] = u
                    } else {
                        channel.asyncSend(null)
                    }
                }
            }
            return channel
        }
    }

    override fun toString(): String {
        return "User(uid=$uid, name='$name', data=$data)"
    }
}