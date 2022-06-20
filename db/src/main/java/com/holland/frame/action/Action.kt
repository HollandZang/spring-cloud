package com.holland.frame.action

import com.holland.JDBCConnectionPool
import com.holland.DataSource
import com.holland.frame.BaseRepository
import com.holland.frame.Field
import com.holland.util.DbUtil

abstract class Action<T : BaseRepository<T>>(
    protected val sql: StringBuilder = StringBuilder(),
    protected val base: BaseRepository<T>,
    protected val pool: JDBCConnectionPool,
    protected val aClass: Class<T>,
    protected val fields: List<Field>,
    protected val dataSource: DataSource,
    protected val params: MutableList<Any> = mutableListOf(),
) {

    fun execute(): List<T> {
        return pool.use({
            prepareStatement(getSql())
                .apply {
                    params.forEachIndexed { index, any ->
                        setObject(index + 1, any)
                    }
                }
        }, { DbUtil.getResult(this, clazz = aClass) })!!
    }

    fun executeAny(): List<Map<String, *>> {
        return pool.use({
            prepareStatement(getSql())
                .apply {
                    params.forEachIndexed { index, any ->
                        setObject(index + 1, any)
                    }
                }
        }, { DbUtil.getResult(this) })!!
    }

    protected abstract fun updateSql(): StringBuilder
    abstract fun getSql(): String
}