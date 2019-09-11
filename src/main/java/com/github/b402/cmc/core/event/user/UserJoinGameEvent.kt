package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Cancellable
import com.github.b402.cmc.core.event.Event

class UserJoinGameEvent(
        val gid:Int,
        val uid:Int,
        val joinCancel:Boolean
): Event(),Cancellable {
    override var isCancel: Boolean = false
    override var reason: String? = null
}