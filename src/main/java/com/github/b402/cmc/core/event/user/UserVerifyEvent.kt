package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Event

class UserVerifyEvent(
        val uid: Int,
        val operator: Int,
        val verify:Boolean
) : Event()