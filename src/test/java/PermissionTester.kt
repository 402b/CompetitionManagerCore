import com.github.b402.cmc.core.Permission
import org.junit.Assert
import org.junit.Test

class PermissionTester {
    @Test
    fun onTest(){
        Assert.assertTrue(Permission.ADMIN !in Permission.ANY)
        Assert.assertTrue(Permission.ANY in Permission.ADMIN)
        Assert.assertTrue(Permission.USER in Permission.ADMIN)
        Assert.assertTrue(Permission.ADMIN.contains(Permission.JUDGE))
    }
}