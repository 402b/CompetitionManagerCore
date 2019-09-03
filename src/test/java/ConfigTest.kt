import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.configuration.MemorySection
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.token.Token
import com.github.b402.cmc.core.token.TokenManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
class ConfigTest {

    @Before
    fun init(){
        SQLManager.init()
        TokenManager.init()
    }

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
                , arrayOf(1, 2, 3))
    }

    @Test
    fun token() = runBlocking{
        Assert.assertEquals(TokenManager.Signature, "9112D7490C0480CC7014593DF27CC0A3")
        val uuid = 234
        val token = Token(uuid)
        val testToken = token.toTokenString()
        println("testToken: $testToken")
        val c = Token.deToken(testToken)
        Assert.assertEquals(c, token)
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
      "prepStmtCacheSqlLimit": "2048",
      "useSSL": "true"
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
