import com.github.b402.cmc.core.service.impl.RegisterData
import com.github.b402.cmc.core.service.impl.RegisterService
import com.github.b402.cmc.core.sql.SQLManager
import com.github.b402.cmc.core.sql.data.User
import com.github.b402.cmc.core.token.TokenManager
import com.google.gson.JsonParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test

class UserTest {
    @Before
    fun init() {
        SQLManager.init()
        TokenManager.init()
    }

    @Test
    fun createUser() = runBlocking<Unit> {
        withTimeout(3000L) {
            SQLManager.coroutinesConnection{
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