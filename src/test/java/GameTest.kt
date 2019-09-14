import com.github.b402.cmc.core.service.impl.game.CreateGameData
import com.github.b402.cmc.core.sort.Sort
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.token.TokenManager
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
class GameTest {
    @Before
    fun init() {
        Sort.init()
        SQLManager.init()
        SQLManager.operateConnection {
            val stn = this.createStatement()
            stn.execute("SET FOREIGN_KEY_CHECKS=0")
            stn.execute("TRUNCATE TABLE Game")
            stn.execute("SET FOREIGN_KEY_CHECKS=1")
        }
        TokenManager.init()
    }

    @Test
    fun test() = runBlocking {
        val json = """
            {
                "name": "测试",
                "time" : ${1567499043847},
                "number": 6,
                "startDate": ${1567499043847 + 100000},
                "endDate": ${1567499043847 + 10000000},
                "sortType": "SecondAsecSort"
            }
        """.trimIndent()
        val j = JsonParser().parse(json)
        val game = Game.createGame(CreateGameData(j.asJsonObject))
        Assert.assertEquals("Data(data1=Game(id=1, name='测试', data={\"amount\":6,\"sortType\":\"SecondAsecSort\",\"startTime\":1567499143847,\"time\":1567499043847,\"endTime\":1567509043847}), data2=null)",game.toString())
        Unit
    }

}