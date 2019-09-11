import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.event.EventBus
import org.junit.Test

class ListenerTest {
    open class TestEvent : Event()
    class TestEvent2 : TestEvent()

    @Test
    fun register(){
        val ll = EventBus.registerListener<TestEvent>(EventBus.EventPriority.NORMAL,false,false){

            println("test")
        }

        EventBus.callEvent(TestEvent2())
    }
}