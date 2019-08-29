import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.token.TokenManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.system.measureTimeMillis

class ConfigTest {

    @Test
    fun testJson() {
        val ms = MemorySection("", mutableSetOf())
        ms["name"] = "test"
        ms["number"] = 2
        Assert.assertEquals(ms.toJson(), "{\"name\":\"test\",\"number\":2}")
        Assert.assertEquals(ms.getDouble("number"), 2.0, 1e-9)
        val d = MemorySection.readFromJson("{\"name\":\"test\",\"number\":2}")
        Assert.assertEquals(d, ms)
        Assert.assertNotEquals(ms, MemorySection.readFromJson("{\"name\":\"test\",\"number\":3}"))
    }

    @Test
    fun testComJson() {
        val ms = MemorySection.readFromJson("""
            {
                "Data": {
                    "key": 1,
                    "array": [1,2,3]
                }
            }
        """.trimIndent())
        val cs = ms.getConfigurationSection("Data")
        Assert.assertEquals(cs!!.getInt("key"), 1)
        Assert.assertArrayEquals(cs.getNumberList("array")!!
                .map { it.toInt() }.toTypedArray()
                , arrayOf(1,2,3))
    }

    @Test
    fun token() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjIzNCwiZXhwIjoxNTY3MTQyMjEwMjMyLCJpYXQiOjE1NjY5Njk0MTAyMzIsImp0aSI6MTI4OTQzMX0=.0A9D3DE82D08F9490E64CE321CC01834"

        TokenManager.init()
        Assert.assertEquals(TokenManager.Signature, "9112D7490C0480CC7014593DF27CC0A3")
        val uuid = 234
        val time = 1566969410232L
        val token = Token(uuid, time + 48L * 60L * 60L * 1000L, time, 1289431L)
        Assert.assertEquals(token.toTokenString(), testToken)
        val c = Token.deToken(testToken)
        Assert.assertEquals(c, token)
    }

    @Test
    fun touristToken() {
        TokenManager.init()
        val tt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOi0xLCJleHAiOjAsImlhdCI6MCwianRpIjowfQ==.D2DE9C919F12FF28AE46182C533D0BCB"
        val token = Token(-1, 0, 0, 0)
        Assert.assertEquals(token.toTokenString(), tt)
        val c = Token.deToken(tt)
        Assert.assertEquals(c, token)
        Assert.assertTrue(c?.isTourist ?: false)
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
