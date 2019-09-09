package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SubmitData

object GameCompleteService :DataService<SubmitData>(
        "game_complete",
        Permission.VERIFIED,
        SubmitData::class.java
){
    override suspend fun onRequest(data: SubmitData): ReturnData {
        TODO("not implemented")
    }
}