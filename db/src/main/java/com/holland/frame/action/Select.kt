package com.holland.frame.action

import com.holland.DataSource
import com.holland.JDBCConnectionPool
import com.holland.frame.BaseRepository
import com.holland.frame.Field
import com.holland.frame.annotation.Table

class Select<T : BaseRepository<T>>(
    base: BaseRepository<T>,
    pool: JDBCConnectionPool,
    aClass: Class<T>,
    fields: List<Field>,
    dataSource: DataSource,
    private val tableName: String,
    private var values: String? = null,
    private var table: Table,
) : Action<T>(StringBuilder(), base, pool, aClass, fields, dataSource) {

    fun where() = Where(updateSql(), base, pool, aClass, fields, dataSource)

    fun groupBy(values: String) = GroupBy(updateSql(), base, pool, aClass, fields, dataSource, values)

    fun order(values: String) = Order(updateSql(), base, pool, aClass, fields, dataSource, values)

    fun limit(limit: Int, offset: Int) = Limit(updateSql(), base, pool, aClass, fields, dataSource, limit, offset)

    override fun updateSql(): StringBuilder = StringBuilder(getSql())

    override fun getSql(): String {
        val tableName = dataSource.tableFullName(table, tableName)
        return if (values == null) "select * from $tableName "
        else {
            "select $values from $tableName "
        }
    }
}