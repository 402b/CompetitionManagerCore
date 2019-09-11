package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Event

class UserVerifyJoinGameEvent(
        val gid:Int,
        val uid:Int,
        val operator: Int,
        val verify:Boolean
): Event()