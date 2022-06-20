package com.holland.frame

import com.holland.frame.annotation.Table

object Cache {
    val classReflect = mutableMapOf<Class<*>, CacheClassReflect>()
}

class CacheClassReflect(val table: Table, val fields: List<Field>)