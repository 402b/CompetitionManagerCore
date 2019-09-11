package com.github.b402.cmc.core.event

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.*

object EventBus {
    private val handlers = mutableMapOf<Class<out Event>, EventHandler>()

    fun registerListener(listener: Any) {
        for (method in listener::class.java.declaredMethods) {
            method.isAccessible = true
            if (method.isAnnotationPresent(Listener::class.java)) {
                val anno = method.getAnnotation(Listener::class.java)
                val parameters = method.parameters
                if (parameters.size != 1) {
                    Logger.getLogger(EventBus::class.java).warn("警告 监听器@${listener::class.java.name}#${method.name} 参数数量不正确 无法注册")
                    continue
                }
                val para = parameters[0]
                if (!Event::class.java.isAssignableFrom(para.type)) {
                    Logger.getLogger(EventBus::class.java).warn("警告 监听器@${listener::class.java.name}#${method.name} 参数类型不正确 无法注册")
                    continue
                }
                val event: Class<out Event> = para.type as Class<out Event>
                registerListener(listener, anno.value, event, anno.ignoreCancel, anno.async, method)
            }
        }
    }

    inline fun <reified E : Event> registerListener(priority: EventPriority, ignoreCancel: Boolean, async: Boolean, crossinline func: (E) -> Unit): LambdaListener<E> {
        val ll = object : LambdaListener<E>(E::class.java) {
            override fun onCall(e: Event) {
                if (e is E)
                    func(e)
            }
        }
        val method = ll::class.java.getMethod("onCall", Event::class.java)
        registerListener(ll, priority, E::class.java, ignoreCancel, async, method)
        return ll
    }

    fun registerListener(listener: Any, priority: EventPriority, event: Class<out Event>, ignoreCancel: Boolean, async: Boolean, method: Method) {
        method.isAccessible = true
        val handler = handlers.getOrPut(event) { EventHandler(event) }

        val wl = WrapListener(event, MethodHandles.lookup().unreflect(method), listener, ignoreCancel, async)
        val list = handler.handler.getOrPut(priority) { mutableListOf() }
        list.add(wl)
    }

    fun callEvent(e: Event) {
        fireEvent(e::class.java, e)
        GlobalScope.launch {
            fireEventAsync(e::class.java, e)
        }
    }

    private suspend fun fireEventAsync(event: Class<out Event>, e: Event) {
        val sc = event.superclass
        if (Event::class.java.isAssignableFrom(sc)) {
            val tclass = sc as? Class<out Event>
            if (tclass != null)
                fireEventAsync(tclass, e)
        }
        val handler = handlers[event] ?: return
        handler.callEventAsync(e)
    }

    private fun fireEvent(event: Class<out Event>, e: Event) {
        val sc = event.superclass
        if (Event::class.java.isAssignableFrom(sc)) {
            val tclass = sc as? Class<out Event>
            if (tclass != null)
                fireEvent(tclass, e)
        }
        val handler = handlers[event] ?: return
        handler.callEvent(e)
    }

    abstract class LambdaListener<E : Event>(
            val event: Class<out E>
    ) {
        abstract fun onCall(e: Event)
    }

    class EventHandler(
            val event: Class<out Event>
    ) {
        val handler: MutableMap<EventPriority, MutableList<WrapListener>> = EnumMap(EventPriority::class.java)

        fun callEventAsync(e: Event) {
            if (event.isAssignableFrom(e::class.java)) {
                for (ep in EventPriority.values()) {
                    val list = handler[ep] ?: continue
                    for (wl in list) {
                        if (!wl.async) {
                            continue
                        }
                        if (e is Cancellable) {
                            if (e.isCancel && wl.ignoreCancel) {
                                continue
                            }
                        }
                        wl(e)
                    }
                }
            }
        }

        fun callEvent(e: Event) {
            if (event.isAssignableFrom(e::class.java)) {
                for (ep in EventPriority.values()) {
                    val list = handler[ep] ?: continue
                    for (wl in list) {
                        if (wl.async) {
                            continue
                        }
                        if (e is Cancellable) {
                            if (e.isCancel && wl.ignoreCancel) {
                                continue
                            }
                        }
                        wl(e)
                    }
                }
            }
        }

    }

    enum class EventPriority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST,
        MORMAL
    }


    data class WrapListener(
            val event: Class<out Event>,
            var method: MethodHandle,
            val listener: Any,
            val ignoreCancel: Boolean,
            val async: Boolean
    ) {
        init {
            method = method.bindTo(listener)
        }

        operator fun invoke(e: Event) {
            if (event.isInstance(e)) {
                method.invokeWithArguments(e)
            }
        }
    }
}