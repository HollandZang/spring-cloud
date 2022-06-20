package com.holland.util

import java.io.File
import java.util.*

object PropertiesUtil {
    val map = mutableMapOf<String, Set<Properties>>()

    fun load(path: String = "", fileName: String) {
        val properties = Properties()
        val fullName = if (path != "" && path.endsWith(File.separatorChar)) {
            path + fileName
        } else {
            "$path${File.separatorChar}$fileName"
        }
        properties.load(File(fullName).reader())

        map[fileName] = map[fileName]?.plus(properties) ?: setOf(properties)
        map[fullName] = map[fullName]?.plus(properties) ?: setOf(properties)
    }

    fun get(fileName: String, key: String): String? {
        return map[fileName]
            ?.mapNotNull { it[key] }
            ?.first()
            ?.toString()
    }
}

fun main() {
    PropertiesUtil.run {
        load("conf\\sql\\", "mysql.properties")
    }
    val value = PropertiesUtil.get("mysql.properties", "fetchTable")
    println(value)
}