import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.token.TokenManager
import org.junit.Assert
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class ConfigTest {

    @Test
    fun token() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjI5MGJlOTQ1LTI5Y2ItNGNhNS04ZmEzLWRkYWIyZTIyZDBlMSwiZXhwIjoxNTY3MTQyMjEwMjMyLCJpYXQiOjE1NjY5Njk0MTAyMzIsImp0aSI6MTI4OTQzMX0=.2F00670609C9F1A53BD9EB6B204E5F66"
        TokenManager.init()
        Assert.assertEquals(TokenManager.Signature, "9112D7490C0480CC7014593DF27CC0A3")
        val uuid = UUID.fromString("290be945-29cb-4ca5-8fa3-ddab2e22d0e1")
        val time = 1566969410232L
        val token = Token(uuid, time + 48L * 60L * 60L * 1000L, time, 1289431L)
        Assert.assertEquals(token.toTokenString(), testToken)
        val c = Token.deToken(testToken)
        Assert.assertEquals(c, token)
    }

    @Test
    fun touristToken() {
        TokenManager.init()
        val tt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCwiZXhwIjowLCJpYXQiOjAsImp0aSI6MH0=.ABFE3EA5BC38BD30DBBB57D869672A03"
        val token = Token(UUID.fromString("00000000-0000-0000-0000-000000000000"), 0, 0, 0)
        Assert.assertEquals(token.toTokenString(), tt)
        val c = Token.deToken(tt)
        Assert.assertEquals(c, token)
        println(c)
    }

    @Test
    fun onTest() {

        val s = Thread.currentThread().contextClassLoader.getResourceAsStream("config.json")
        val br = BufferedReader(InputStreamReader(s!!))
        var json = ""
        while (true) {
            try {
                val line: String? = br.readLine() ?: break
                json += line + "\n"
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        Assert.assertEquals(json, config)
        val cfg = Configuration(json)
        val sql = cfg.getConfigurationSection("SQL")!!
        Assert.assertEquals(sql.getString("jdbc"), "mysql")
        val opt = sql.getConfigurationSection("options")!!
        for (key in opt.keys!!) {
            println("$key: ${opt.getString(key)}")
        }
    }


    companion object {
        const val config = """{
  "SQL": {
    "jdbc": "mysql",
    "host": "localhost",
    "port": 3306,
    "database": "cmc",
    "user": "root",
    "password": "123456",
    "options": {
      "useUnicode": "true",
      "characterEncoding": "utf8",
      "autoReconnect": "true",
      "rewriteBatchedStatements": "true",
      "cachePrepStmts": "true",
      "prepStmtCacheSize": "250",
      "prepStmtCacheSqlLimit": "2048"
    },
    "HikariCP": {
      "IdleTimeout": 60000,
      "ConnectionTimeout": 60000,
      "ValidationTimeout": 3000,
      "MaxLifetime": 60000,
      "MaximumPoolSize": 50
    }
  }
}
"""
    }
}
