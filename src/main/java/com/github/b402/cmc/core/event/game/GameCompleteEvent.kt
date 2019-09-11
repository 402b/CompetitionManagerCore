package com.github.b402.cmc.core.event.game

import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.sql.data.Game
import com.github.b402.cmc.core.sql.data.GameResult

class GameCompleteEvent(
        val game:Game
): Event() {
}