package com.github.b402.cmc.core.event.user

import com.github.b402.cmc.core.event.Cancellable
import com.github.b402.cmc.core.event.Event
import com.github.b402.cmc.core.sql.data.UserGender

class UserRegisterEvent(
        val uid: Int,
        val id: String,
        val gender: UserGender,
        val userName: String
) : Event(),Cancellable{
    override var isCancel: Boolean = false
    override var reason: String? = null
}