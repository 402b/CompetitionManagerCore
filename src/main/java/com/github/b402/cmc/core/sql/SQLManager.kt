package com.github.b402.cmc.core.sql

import com.github.b402.cmc.core.FileManager
import com.github.b402.cmc.core.configuration.Configuration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.log4j.Logger
import java.io.File
import java.sql.Connection
import java.sql.SQLException

object SQLManager {
    private lateinit var connectPool: HikariDataSource
    private var init = false
    fun init() {
        if (init) return
        init = true
        Logger.getLogger(SQLManager::class.java).info("开始初始化数据库连接池")
        try {
            val configs = Thread.currentThread().contextClassLoader.getResourceAsStream("config.json")!!
            val file = File(FileManager.getBaseFolder(), "config.yml")
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

    private fun checkTable() {
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
        }
    }

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
}