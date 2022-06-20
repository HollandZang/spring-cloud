@file:Suppress("SqlDialectInspection", "SqlNoDataSourceInspection")

package com.holland.frame

import com.holland.JDBCConnectionPool
import com.holland.DataSource
import com.holland.frame.annotation.Table
import com.holland.frame.service.MysqlGenerator
import com.holland.frame.service.PostgresGenerator

interface Generator {

    companion object {
        private val gDict = mutableMapOf<DataSource, Generator>()

        fun doGenerate(it: Class<*>) {
            val table = it.getDeclaredAnnotation(Table::class.java)
                ?: return
            val pool = DbConf.m[table.datasource]

            val p = gDict[pool!!.dataSource]
                ?: kotlin.run {
                    val t = when (pool.dataSource) {
                        DataSource.POSTGRES -> PostgresGenerator()
                        DataSource.MYSQL -> MysqlGenerator()
                        DataSource.ORACLE -> null
                    }
                    if (t == null) {
                        System.err.println("> Generator template not load: " + table.datasource)
                        return
                    } else {
                        gDict[pool.dataSource] = t
                        t
                    }
                }

            val field = it.declaredFields.map { Field(it) }
            p.create(it, pool, table, field)
        }
    }

    fun create(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>)
}

fun main() {

    val classList = arrayListOf(TestClass::class.java)

    classList.forEach(Generator::doGenerate)
}
