package com.holland.frame

import com.holland.frame.annotation.Column
import com.holland.frame.annotation.Index
import com.holland.frame.annotation.PrimaryKey
import com.holland.frame.annotation.Indexes
import java.lang.reflect.Field

class Field(val field: Field) {
    val clazz: Class<*>
    val name: String
    val type: Class<*>
    val primaryKey: PrimaryKey?
    val index: Array<Index>?
    val column: Column

    init {
        this.clazz = field::class.java
        this.name = field.name
        this.type = field.type
        this.primaryKey = field.getDeclaredAnnotation(PrimaryKey::class.java)

        val l = mutableListOf<Index>()
        field.getDeclaredAnnotation(Indexes::class.java)
            ?.value
            ?.forEach { l.add(it) }
        val u = field.getDeclaredAnnotation(Index::class.java)
        if (u != null) l.add(u)
        this.index = l.toTypedArray()
        this.column = field.getDeclaredAnnotation(Column::class.java)
    }

    fun getNotNull(boolean: Boolean = true): String {
        val r = arrayOf("NOT NULL", "NULL")
        var i = if (primaryKey != null) 0 else if (column.notNull) 0 else 1
        if (boolean.not()) i = (i + 1) % 2
        return r[i]
    }

    fun getType(): String {
        return if (column.length > 0) {
            if (column.scale > 0) {
                "${column.dbType}(${column.length},${column.scale})"
            } else {
                "${column.dbType}(${column.length})"
            }
        } else {
            column.dbType
        }
    }

    companion object {
        /**
         * @return: Map< Index, List< FieldName > >
         */
        fun getIndexes(fields: List<com.holland.frame.Field>): MutableMap<Index, MutableList<String>> {
            val list = mutableMapOf<Index, MutableList<String>>()
            fields.filter { it.index != null }
                .forEach {
                    it.index!!.forEach { o ->
                        list.computeIfPresent(o) { _, v -> v.apply { this.add(it.name) } }
                        list.putIfAbsent(o, mutableListOf(it.name))
                    }
                }
            return list
        }
    }
}