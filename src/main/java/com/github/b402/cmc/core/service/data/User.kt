package com.github.b402.cmc.core.service.data

import com.github.b402.cmc.core.token.Token

class User(
        val uid: Int
) {
    var lastToken:Token? = null
}