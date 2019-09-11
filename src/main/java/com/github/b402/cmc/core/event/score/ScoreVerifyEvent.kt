package com.github.b402.cmc.core.event.score

import com.github.b402.cmc.core.event.Cancellable
import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.sql.data.Score

class ScoreVerifyEvent(
        val score:Score
) :Event(),Cancellable{
    override var isCancel: Boolean = false
    override var reason: String? = null

}