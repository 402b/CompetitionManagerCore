package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Event

class UserJudgeApplyEvent(
        val uid:Int,
        val gid:Int
) : Event()