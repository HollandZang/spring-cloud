package com.holland.frame.action

import com.holland.JDBCConnectionPool
import com.holland.DataSource
import com.holland.frame.BaseRepository
import com.holland.frame.Field

class Limit<T : BaseRepository<T>>(
    sql: StringBuilder,
    base: BaseRepository<T>,
    pool: JDBCConnectionPool,
    aClass: Class<T>,
    fields: List<Field>,
    dataSource: DataSource,
    private val limit: Int,
    private val offset: Int,
    params: MutableList<Any> = mutableListOf(),
) : Action<T>(sql, base, pool, aClass, fields, dataSource, params) {

    override fun updateSql(): StringBuilder {
        return sql.append("limit $limit,$offset ")
    }

    override fun getSql(): String {
        return updateSql().toString()
    }
}