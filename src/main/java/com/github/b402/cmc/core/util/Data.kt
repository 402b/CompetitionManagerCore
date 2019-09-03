package com.github.b402.cmc.core.util

import kotlinx.coroutines.channels.Channel

data class Data<R,V>(
        val data1:R,
        val data2:V
)