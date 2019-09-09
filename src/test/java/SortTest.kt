import com.github.b402.cmc.core.sort.impl.*

import org.junit.Assert
import org.junit.Test

class SortTest {

    @Test
    fun testSecondAsecSort() {
        val tas = SecondAsecSort()
        Assert.assertNull(tas.getScore(""))
        val s1 = tas.getScore("1:1'1\"22")!!
        val s2 = tas.getScore("1:1'1\"21")!!
        Assert.assertTrue(s1 > s2)
    }


    @Test
    fun testSecondDescSort() {
        val tas = SecondDescSort()
        Assert.assertNull(tas.getScore(""))
        val s1 = tas.getScore("1:1'1\"22")!!
        val s2 = tas.getScore("1:1'1\"21")!!
        Assert.assertTrue(s1 < s2)
    }

    @Test
    fun testTimeAsecSort() {
        val tas = TimesAsecSort()
        Assert.assertNull(tas.getScore(""))
        Assert.assertNull(tas.getScore("-1"))
        val s1 = tas.getScore("2")!!
        val s2 = tas.getScore("1")!!
        Assert.assertTrue(s1 > s2)
    }

    @Test
    fun testTimeDescSort() {
        val tas = TimesDescSort()
        Assert.assertNull(tas.getScore(""))
        Assert.assertNull(tas.getScore("-1"))
        val s1 = tas.getScore("2")!!
        val s2 = tas.getScore("1")!!
        Assert.assertTrue(s1 < s2)
    }

    @Test
    fun testLengthAsecSort() {
        val tas = LengthAsecSort()
        Assert.assertNull(tas.getScore(""))
        val s1 = tas.getScore("2.15")!!
        val s2 = tas.getScore("2.1499")!!
        Assert.assertTrue(s1 > s2)
    }

    @Test
    fun testLengthDescSort() {
        val tas = LengthDescSort()
        Assert.assertNull(tas.getScore(""))
        val s1 = tas.getScore("0.1499")!!
        val s2 = tas.getScore("0.15")!!
        Assert.assertTrue(s1 > s2)
    }


}
