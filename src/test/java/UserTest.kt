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
import org.junit.Test

class UserTest {
    @Test
    fun createUser() = runBlocking<Unit> {
        SQLManager.init()
        TokenManager.init()
        val rs = RegisterService()
        val resp = Channel<String?>()
        val result = rs.input("""
            {
                "Data": {
                    "realName": "林zh",
                    "gender": "男",
                    "id": "9177d25",
                    "userName": "Bryanlzh",
                    "password": "123456"
                }
            }
        """.trimIndent(),this)
        println(result.toJson())
    }
}