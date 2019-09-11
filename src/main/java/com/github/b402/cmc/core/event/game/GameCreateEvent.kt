package com.github.b402.cmc.core.event.game

import com.github.b402.cmc.core.event.Cancellable
import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.service.impl.game.CreateGameData

class GameCreateEvent(
        val data: CreateGameData
) : Event(), Cancellable {
    override var isCancel: Boolean = false
    override var reason: String? = null
}