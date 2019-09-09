package com.github.b402.cmc.core.service.impl.game

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SUCCESS
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.core.sort.Sort
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object SortListService : DataService<SubmitData>(
        "sort_list",
        Permission.ANY,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        return returnData(SUCCESS) {
            val arr = JsonArray()
            for (name in Sort.getAllSortName()) {
                val obj = JsonObject()
                obj.addProperty("sortName", name)
                obj.addProperty("display", Sort.getSort(name)!!.getDisplayName());
                arr.add(obj)
            }
            json.add("sorts", arr)
        }
    }
}