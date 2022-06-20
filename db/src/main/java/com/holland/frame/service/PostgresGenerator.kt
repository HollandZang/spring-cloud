package com.holland.frame.service

import com.holland.JDBCConnectionPool
import com.holland.frame.Field
import com.holland.frame.Ftl
import com.holland.frame.Generator
import com.holland.frame.IncrementStrategy
import com.holland.frame.annotation.Table
import com.holland.util.DbUtil

class PostgresGenerator : Generator {
    override fun create(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>) {
        checkTable(clazz, db, table, fields)
    }

    private fun checkTable(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>) {
        val tableName = clazz.simpleName
        val tableFullName = db.dataSource.tableFullName(table, tableName)

        val checkSql =
            Ftl.generate(db.dataSource, "checkTable.ftl", mapOf("tableName" to tableName, "schema" to table.schema))
        println("> Check: $checkSql")
        val list = db.use(
            { prepareStatement(checkSql) },
            { DbUtil.getResult(this) }
        )

        if (list!!.isEmpty())
            createIt(clazz, db, table, fields)
        else {
            if (list[0]["table_comment"] != table.comment) {
                val updateTableCommentSql = "COMMENT ON TABLE $tableFullName IS '${table.comment}'"
                println("> updateTableComment: $updateTableCommentSql")
                db.use { prepareStatement(updateTableCommentSql) }
            }

//            checkItems(clazz, db, table, fields)
        }
    }

//    private fun checkItems(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>) {
//
//    }

    private fun createIt(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>) {
        val tableName = clazz.simpleName
        val tableFullName = db.dataSource.tableFullName(table, tableName)
        val keyPrefix = "${table.db}_${table.schema}_${tableName}"

        val builder = StringBuilder()
        builder.append("create table $tableFullName")

        builder.append(
            fields.joinToString(",", "(",)
            {
                if (it.primaryKey != null) {
                    val type =
                        if (it.primaryKey.autoIncrement==IncrementStrategy.AUTO) "serial"
                        else it.getType()
                    "${it.name} $type NOT NULL"
                } else {
                    val (type, nullable) =
                        it.getType() to if (it.column.notNull) "NOT NULL" else "NULL"
                    "${it.name} $type $nullable"
                }
            }
        )

        fields.filter { it.primaryKey != null }
            .forEachIndexed { i, f ->
                builder.append(",CONSTRAINT ${keyPrefix}_pk_${i} PRIMARY KEY (${f.name})")
            }

        @Suppress("LocalVariableName")
        val index_fieldNameList = mutableMapOf<String, MutableList<String>>()
        fields.filter { it.index != null }
            .forEach {
                it.index!!.forEach { o ->
                    index_fieldNameList.computeIfPresent(o.indexName) { _, v -> v.apply { this.add(it.name) } }
                    index_fieldNameList.putIfAbsent(o.indexName, mutableListOf(it.name))
                }
            }
        index_fieldNameList.forEach { (indexName, f) ->
            val unique = f.joinToString(",", "(", ")")
            builder.append(",CONSTRAINT ${keyPrefix}_$indexName UNIQUE $unique")
        }

        builder.append(")")

        println("> Create table: $builder")
        try {
            db.use { prepareStatement(builder.toString()) }
        } catch (ignore: Exception) {
            if (ignore.message != "resultSet must not be null")
                ignore.printStackTrace()
        }
        println("> Create table: [${"postgres.public.$tableName"}] successfully")

        db.use { prepareStatement("COMMENT ON TABLE $tableFullName IS '${table.comment}';") }
        fields.forEach {
            db.use { prepareStatement("comment on column ${tableFullName}.${it.name} is '${it.column.comment}';") }
        }
    }
}