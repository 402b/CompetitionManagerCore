package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Event

class UserVerifyJudgeEvent(
        val uid:Int,
        val gid:Int,
        val operator: Int,
        val verify:Boolean
): Event()