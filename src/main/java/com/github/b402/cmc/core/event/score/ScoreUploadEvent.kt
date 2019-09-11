package com.github.b402.cmc.core.event.score

import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.sql.data.Score

class ScoreUploadEvent(
        val score: Score
): Event()