package com.holland.util

object TimeUtil {

    fun <T> printMethodTime(methodName: String, method: () -> T): T {
        println("-- start $methodName")
        val nanoTime = System.currentTimeMillis()
        val result = method.invoke()
        println("-- end $methodName cost ${(System.currentTimeMillis() - nanoTime) / 1000}s")
        return result
    }
}
