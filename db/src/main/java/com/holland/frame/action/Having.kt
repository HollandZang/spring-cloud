package com.holland.frame.action

import com.holland.JDBCConnectionPool
import com.holland.DataSource
import com.holland.frame.BaseRepository
import com.holland.frame.Field

class Having<T : BaseRepository<T>>(
    sql: StringBuilder,
    base: BaseRepository<T>,
    pool: JDBCConnectionPool,
    aClass: Class<T>,
    fields: List<Field>,
    dataSource: DataSource,
    private val values: String,
    params: MutableList<Any> = mutableListOf(),
) : Action<T>(sql, base, pool, aClass, fields, dataSource, params) {

    fun order(values: String): Order<T> {
        return Order(updateSql(), base, pool, aClass, fields, dataSource, values, params)
    }

    override fun updateSql(): StringBuilder {
        return sql.append("having $values ")
    }

    override fun getSql(): String {
        return updateSql().toString()
    }
}