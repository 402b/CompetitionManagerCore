package com.github.b402.cmc.core.service.data

import com.github.b402.cmc.core.token.Token
import java.util.*

class User(
        val uuid: UUID
) {
    var lastToken:Token? = null
}