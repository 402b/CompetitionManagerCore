package com.github.b402.cmc.core.sql

import com.github.b402.cmc.core.FileManager
import com.github.b402.cmc.core.configuration.Configuration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import java.io.File
import java.sql.Connection
import java.sql.SQLException
import kotlin.coroutines.coroutineContext

object SQLManager {
    private lateinit var connectPool: HikariDataSource
    private var init = false
    @JvmStatic
    fun init() {
        if (init) return
        init = true
        Logger.getLogger(SQLManager::class.java).info("开始初始化数据库连接池")
        try {
            val configs = Thread.currentThread().contextClassLoader.getResourceAsStream("config.json")!!
            val file = File(FileManager.getBaseFolder(), "config.json")
            if (!file.exists()) {
                FileManager.saveResources(file, configs)
            }
            val config = Configuration(file).getConfigurationSection("SQL")!!
            val url = "jdbc:${
            config.getString("jdbc")
            }://${
            config.getString("host")
            }:${
            config.getInt("port", 3306)
            }/${config.getString("database")}?user=${config.getString("user")}&password=${
            config.getString("password")
            }"
            val cfg = HikariConfig()
            try {
                Class.forName("com.mysql.jdbc.Driver")
            } catch (t: Throwable) {
            }
            cfg.jdbcUrl = url
            config.getConfigurationSection("options")?.let { c ->
                c.keys?.forEach {
                    cfg.addDataSourceProperty(it, c.getString(it))
                }
            }
            config.getConfigurationSection("HikariCP")?.let {
                cfg.idleTimeout = it.getLong("IdleTimeout", 60000L)
                cfg.connectionTimeout = it.getLong("ConnectionTimeout", 60000L)
                cfg.validationTimeout = it.getLong("ValidationTimeout", 3000L)
                cfg.maxLifetime = it.getLong("MaxLifetime", 60000L)
                cfg.maximumPoolSize = it.getInt("MaximumPoolSize", 50)
            }
            connectPool = HikariDataSource(cfg)
            checkTable()
        } catch (t: Throwable) {
            Logger.getLogger(SQLManager::class.java).error("初始化数据库中发生错误", t)
        }
    }

    fun checkTable() {
        Logger.getLogger(SQLManager::class.java).info("正在检查数据表")
        operateConnection {
            val stn = this.createStatement()
            stn.execute("""
                CREATE TABLE IF NOT EXISTS User(
                 UID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                 Name VARCHAR(30) NOT NULL,
                 Data Json NOT NULL
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.execute("""
                CREATE TABLE IF NOT EXISTS Game(
                    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    Name VARCHAR(80) NOT NULL,
                    Data Json NOT NULL,
                    Archive  BOOLEAN NOT NULL DEFAULT FALSE
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.execute("""
                CREATE TABLE IF NOT EXISTS JoinGame(
                    UID INT NOT NULL,
                    GID INT NOT NULL,
                    Time LONG NOT NULL,
                    Verified BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY (UID,GID),
                    FOREIGN KEY (UID) REFERENCES User(UID),
                    FOREIGN KEY (GID) REFERENCES Game(ID)
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.execute("""
                CREATE TABLE IF NOT EXISTS Judge(
                    UID INT NOT NULL,
                    GID INT NOT NULL,
                    Type VARCHAR(10) NOT NULL,
                    Verified BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY (UID,GID),
                    FOREIGN KEY (UID) REFERENCES User(UID),
                    FOREIGN KEY (GID) REFERENCES Game(ID)
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.execute("""
                CREATE TABLE IF NOT EXISTS Score(
                    UID INT NOT NULL,
                    GID INT NOT NULL,
                    Score VARCHAR(255) NOT NULL,
                    SubmitJudge INT NOT NULL,
                    Verified BOOLEAN NOT NULL DEFAULT FALSE,
                    VerifiedJudge INT DEFAULT NULL,
                    PRIMARY KEY (UID,GID),
                    FOREIGN KEY (UID) REFERENCES User(UID),
                    FOREIGN KEY (SubmitJudge) REFERENCES User(UID),
                    FOREIGN KEY (VerifiedJudge) REFERENCES User(UID),
                    FOREIGN KEY (GID) REFERENCES Game(ID)
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.execute("""
                CREATE TABLE IF NOT EXISTS GameResult(
                    GID INT NOT NULL PRIMARY KEY,
                    Result JSON NOT NULL,
                    Time LONG NOT NULL,
                    FOREIGN KEY (GID) REFERENCES Game(ID)
                ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
            """.trimIndent())
            stn.close()
        }
    }

    @JvmStatic
    fun operateConnection(func: Connection.() -> Unit) {
        var conn: Connection? = null
        try {
            conn = connectPool.connection
            conn.func()
        } catch (e: SQLException) {
            Logger.getLogger(SQLManager::class.java).error("执行数据库回调中发生错误", e)
        } finally {
            if (conn != null) {
                connectPool.evictConnection(conn)
            }
        }
    }

    suspend fun coroutinesConnection(func: suspend Connection.() -> Unit) {
        var conn: Connection? = null
        try {
            conn = connectPool.connection
            conn.func()
        } catch (e: SQLException) {
            Logger.getLogger(SQLManager::class.java).error("执行数据库回调中发生错误", e)
        } finally {
            if (conn != null) {
                connectPool.evictConnection(conn)
            }
        }
    }

    suspend fun async(func: suspend Connection.() -> Unit): Deferred<Boolean> {
        return GlobalScope.async(coroutineContext) {
            var conn: Connection? = null
            try {
                conn = connectPool.connection
                conn.func()
                return@async true
            } catch (e: SQLException) {
                Logger.getLogger(SQLManager::class.java).error("执行数据库回调中发生错误", e)
            } finally {
                if (conn != null) {
                    connectPool.evictConnection(conn)
                }
            }
            false
        }
    }

    suspend fun <R> asyncDeferred(func: suspend Connection.() -> R?): Deferred<R?> {
        return GlobalScope.async(coroutineContext) {
            var conn: Connection? = null
            try {
                conn = connectPool.connection
                return@async conn.func()
            } catch (e: SQLException) {
                Logger.getLogger(SQLManager::class.java).error("执行数据库回调中发生错误", e)
            } finally {
                if (conn != null) {
                    connectPool.evictConnection(conn)
                }
            }
            null
        }
    }
}