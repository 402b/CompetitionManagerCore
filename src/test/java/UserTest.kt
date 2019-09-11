import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.impl.user.RegisterService
import com.github.b402.cmc.core.service.impl.user.UnverifiedUsersService
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.token.TokenManager
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class UserTest {
    @Before
    @Test
    fun init() {
        SQLManager.init()
        TokenManager.init()
    }

    @Test
    fun getUser() = runBlocking {
        val user = User.getUserByName("Bryanlzh")
        val u = user.await()
        println(u!!.data.toJson())
    }

    @Test
    fun unverified() = runBlocking {
        val d = UnverifiedUsersService.onRequest(SubmitData(JsonParser().parse("{}").asJsonObject))
        println(d.toString())
    }

    @Test
    fun createUser() = runBlocking<Unit> {
        withTimeout(3000L) {
            SQLManager.coroutinesConnection {
                this.prepareStatement("DELETE FROM User WHERE Name = 'Bryanlzh'").executeUpdate()
            }
            val rs = RegisterService
            val result = rs.input("""
            {
                "Data": {
                    "realName": "林zh",
                    "gender": "M",
                    "id": "9177d25",
                    "userName": "Bryanlzh",
                    "password": "123456"
                }
            }
        """.trimIndent())
            println(result.toJson())
        }
    }
}