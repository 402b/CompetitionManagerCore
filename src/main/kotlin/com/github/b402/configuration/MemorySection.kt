package com.github.b402.configuration

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

open class MemorySection(
        override val name: String,
        override var keys: Set<String>?
) : ConfigurationSection {
    override fun save(): JsonObject {
        val obj = JsonObject()
        if (keys == null) {
            return obj
        }
        for (key in keys!!) {
            val any = map[key]
            if (any is ConfigurationSection) {
                obj.add(key, any.save())
            } else {
                if (any is JsonElement) {
                    obj.add(key, any)
                }
            }
        }
        return obj
    }

    protected val map: MutableMap<String, Any> = LinkedHashMap()

    protected constructor() : this("", null)

    protected fun init(json: JsonObject) {
        keys = json.keySet()
        map.clear()
        for (key in json.keySet()) {
            val get = json.get(key)
            if (get.isJsonObject) {
                map[key] = MemorySection(get as JsonObject)
            } else {
                map[key] = get
            }
        }
    }

    constructor(json: JsonObject) : this("", json.keySet()) {
        for (key in json.keySet()) {
            val get = json.get(key)
            if (get.isJsonObject) {
                map[key] = MemorySection(get as JsonObject)
            } else {
                map[key] = get
            }
        }
    }

    fun getJson(path: String): JsonElement? {
        if (path.isEmpty()) {
            return null
        }
        val paths = path.split(",".toRegex(), 2)
        val any = map[paths[0]] ?: return null
        if (paths.size == 1) {
            if (any is JsonElement) {
                return any
            }
            return null
        }
        if (any is MemorySection) {
            return any.getJson(paths[1])
        }
        return null
    }

    private fun getPrimitive(path: String): JsonPrimitive? {
        val json = getJson(path) ?: return null
        return if (json.isJsonPrimitive) {
            json.asJsonPrimitive
        } else {
            null
        }
    }

    private fun getArray(path: String): JsonArray? {
        val json = getJson(path) ?: return null
        return if (json.isJsonArray) {
            json.asJsonArray
        } else {
            null
        }
    }

    override fun getBoolean(path: String, def: Boolean): Boolean {
        return getPrimitive(path)?.asBoolean ?: def
    }

    override fun getInt(path: String, def: Int): Int {
        return getPrimitive(path)?.asNumber?.toInt() ?: def
    }

    override fun getDouble(path: String, def: Double): Double {
        return getPrimitive(path)?.asNumber?.toDouble() ?: def
    }

    override fun getLong(path: String, def: Long): Long {
        return getPrimitive(path)?.asNumber?.toLong() ?: def
    }

    override fun getString(path: String, def: String?): String? {
        return getPrimitive(path)?.asString ?: def
    }

    override fun getStringList(path: String): List<String>? {
        val array = getArray(path) ?: return null
        val list = mutableListOf<String>()
        for (v in array) {
            if (v.isJsonPrimitive) {
                val jp = v.asJsonPrimitive
                list.add(jp.asString)
                continue
            }
            return null
        }
        return list
    }

    override fun getList(path: String): List<Any>? {
        val array = getArray(path) ?: return null
        val list = mutableListOf<Any>()
        for (v in array) {
            if (v.isJsonPrimitive) {
                val jp = v.asJsonPrimitive
                if (jp.isString) {
                    list.add(jp.asString)
                } else if (jp.isBoolean) {
                    list.add(jp.asBoolean)
                } else if (jp.isNumber) {
                    list.add(jp.asNumber)
                } else if (jp.isBoolean) {
                    list.add(jp.asBoolean)
                }
                continue
            }
            return null
        }
        return list
    }

    override fun getConfigurationSection(path: String): ConfigurationSection? {
        if (path.isEmpty()) {
            return null
        }
        val paths = path.split(",".toRegex(), 2)
        val any = map[paths[0]] ?: return null
        if (paths.size == 1) {
            if (any is MemorySection) {
                return any
            }
            return null
        }
        if (any is MemorySection) {
            return any.getConfigurationSection(paths[1])
        }
        return null
    }

    override fun get(path: String): Any? {
        if (path.isEmpty()) {
            return null
        }
        val paths = path.split(",".toRegex(), 2)
        val any = map[paths[0]] ?: return null
        if (paths.size == 1) {
            if (any is MemorySection) {
                return any
            }
            if (any is JsonElement) {
                if (any.isJsonPrimitive) {
                    val jp = any.asJsonPrimitive
                    if (jp.isString) {
                        return jp.asString
                    } else if (jp.isBoolean) {
                        return jp.asBoolean
                    } else if (jp.isNumber) {
                        return jp.asNumber
                    } else if (jp.isBoolean) {
                        return jp.asBoolean
                    }
                }
                if (any.isJsonArray) {
                    val array = any.asJsonArray
                    val list = mutableListOf<Any>()
                    for (v in array) {
                        if (v.isJsonPrimitive) {
                            val jp = v.asJsonPrimitive
                            if (jp.isString) {
                                list.add(jp.asString)
                            } else if (jp.isBoolean) {
                                list.add(jp.asBoolean)
                            } else if (jp.isNumber) {
                                list.add(jp.asNumber)
                            } else if (jp.isBoolean) {
                                list.add(jp.asBoolean)
                            }
                            continue
                        }
                        return null
                    }
                    return list
                }
            }
            return null
        }
        if (any is MemorySection) {
            return any.get(paths[1])
        }
        return null
    }

    override fun set(path: String, data: Any?) {
        if (path.isEmpty()) {
            return
        }
        val paths = path.split(",".toRegex(), 2)
        if (paths.size == 1) {
            if (data == null) {
                map.remove(paths[0])
                return
            }
            map[paths[0]] = warpObject(data)
            return
        }
        val any = map[paths[0]] ?: return

        if (any is MemorySection) {
            any[paths[1]] = data
        }
    }

    private fun warpObject(data: Any): Any {
        if (data is JsonElement) {
            return data
        }
        if (data is Collection<*>) {
            val arr = JsonArray()
            for (v in data) {
                if (v == null) continue
                if (v is String) {
                    arr.add(v)
                } else if (v is Number) {
                    arr.add(v)
                } else if (v is Boolean) {
                    arr.add(v)
                }
                if (v is JsonElement) {
                    arr.add(v)
                }
            }
            return arr
        }
        throw IllegalArgumentException("无法包装对象")
    }
}