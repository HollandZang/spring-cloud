package com.holland.frame.action

import com.holland.DataSource
import com.holland.JDBCConnectionPool
import com.holland.frame.BaseRepository
import com.holland.frame.Field

class Where<T : BaseRepository<T>>(
    sql: StringBuilder,
    base: BaseRepository<T>,
    pool: JDBCConnectionPool,
    aClass: Class<T>,
    fields: List<Field>,
    dataSource: DataSource,
) : Action<T>(sql, base, pool, aClass, fields, dataSource) {

    private val parts = mutableListOf<String>()

    fun eq(field: String): Where<T> {
        val value = fields.find { it.name == field }!!.field.get(base)
        eq(field, value)
        return this
    }

    fun eq(field: String, value: Any?): Where<T> {
        if (value == null) {
            parts.add(dataSource.quoteName(field) + " is null")
        } else {
            parts.add(dataSource.quoteName(field) + "=?")
            params.add(value)
        }
        return this
    }

    fun and(sql: String, vararg values: Any): Where<T> {
        parts.add(sql)
        params.addAll(values)
        return this
    }

    fun or(sql: String, vararg values: Any): Where<T> {
        parts.add(sql)
        params.addAll(values)
        return this
    }

    fun group(values: String): GroupBy<T> = GroupBy(updateSql(), base, pool, aClass, fields, dataSource, values, params)

    override fun updateSql(): StringBuilder {
        if (parts.isEmpty()) return sql
        val s = parts.joinToString(" and ", "where ", " ")
        return sql.append(s)
    }

    override fun getSql(): String {
        return updateSql().toString()
    }
}
