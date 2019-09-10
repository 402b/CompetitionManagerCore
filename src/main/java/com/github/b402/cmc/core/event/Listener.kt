package com.github.b402.cmc.core.event

annotation class Listener(
        val value:EventBus.EventPriority = EventBus.EventPriority.NORMAL
)