package com.github.b402.cmc.core.configuration

import com.google.gson.JsonObject

interface ConfigurationSection {
    val name: String

    var keys: Set<String>?

    fun getBoolean(path: String, def: Boolean = false): Boolean

    fun getInt(path: String, def: Int = 0): Int

    fun getDouble(path: String, def: Double = 0.0): Double

    fun getLong(path: String, def: Long = 0L): Long

    fun getString(path: String, def: String? = null): String?

    fun getStringList(path: String): List<String>?

    fun getList(path: String): List<Any>?

    fun getConfigurationSection(path: String): ConfigurationSection?

    operator fun get(path: String): Any?

    operator fun set(path: String, data: Any?)

    fun save(): JsonObject
}
