package com.holland.frame.service

import com.holland.JDBCConnectionPool
import com.holland.frame.Field
import com.holland.frame.Generator
import com.holland.frame.IncrementStrategy
import com.holland.frame.annotation.Table
import com.holland.util.DbUtil
import com.holland.util.PropertiesUtil
import java.io.File

class MysqlGenerator : Generator {
    init {
        PropertiesUtil.load("conf${File.separatorChar}sql", "mysql.properties")
    }

    override fun create(clazz: Class<*>, db: JDBCConnectionPool, table: Table, fields: List<Field>) {
        val tableName = clazz.simpleName
        val fullName = "`${table.db}`.`${tableName}`"
        val fetchTable = PropertiesUtil.get("mysql.properties", "fetchTable")!!
        val l = db.use(
            {
                prepareStatement(fetchTable)
                    .apply {
                        setString(1, table.db)
                        setString(2, tableName)
                    }
            },
            { DbUtil.getResult(this) })

        if (l == null || l.isEmpty()) {
            /*create table*/
            val value = fields.joinToString(",") { field ->
//                using
                val key = getPK(field)
                val default = getDefault(field)
                "`${field.name}` ${field.getType()} ${field.getNotNull()} $default $key comment '${field.column.comment}'"
            }
            val createSql = "CREATE TABLE $fullName ($value) comment '${table.comment}';"
            db.use({ prepareStatement(createSql) })

            /*create index*/
            val indexSql = Field.getIndexes(fields)
                .map {
                    val (index, fieldNames) = it
                    val values = fieldNames.joinToString(",", "(", ")") { f -> "`$f`" }
                    val useHash = if (index.indexMethod.equals("hash", true)) "USING HASH" else ""
                    "ADD INDEX ${index.indexType} `${index.indexName}` $values $useHash"
                }.joinToString(",", "ALTER TABLE $fullName ", ";")
            db.use { prepareStatement(indexSql) }
        } else {
            /*update table comment*/
            val t = l[0]
            if (t["TABLE_COMMENT"] != table.comment)
                db.use { prepareStatement("ALTER TABLE $fullName COMMENT = '${table.comment}'") }

            /*update field*/
            val fetchColumns = PropertiesUtil.get("mysql.properties", "fetchColumns")!!
            val columns = db.use(
                {
                    prepareStatement(fetchColumns)
                        .apply {
                            setString(1, table.db)
                            setString(2, tableName)
                        }
                },
                { DbUtil.getResult(this) }) ?: listOf()

            var needUpdatePK = false
            val pkList = mutableListOf<String>()
            var indexType = ""
            fields.forEach { field ->
                val name = field.name
                val find = columns.find { map -> map["COLUMN_NAME"] == name }!!

                /*update PK*/
                val b1 = find["COLUMN_KEY"] == "PRI"
                val b2 = field.primaryKey != null
                if (b2) {
                    pkList.add(name)
                    if (indexType == "" && field.primaryKey!!.indexType != "")
                        indexType = "USING ${field.primaryKey.indexType}"
                    if (indexType != "" && indexType != "USING ${field.primaryKey!!.indexType}")
                        throw RuntimeException("PK indexType must unanimous")
                }
                if (b1 != b2)
                    needUpdatePK = true
            }

            if (needUpdatePK)
                db.use {
                    prepareStatement(
                        "ALTER TABLE $fullName DROP PRIMARY KEY,ADD PRIMARY KEY (${pkList.joinToString(",") { "`$it`" }}) $indexType;"
                    )
                }

            fields.forEach { field ->
                val name = field.name
                val find = columns.find { map -> map["COLUMN_NAME"] == name }
                if (find == null) {
                    val key = getPK(field)
                    val default = getDefault(field)
                    val sql =
                        "ALTER TABLE $fullName ADD COLUMN `$name` ${field.getType()} ${field.getNotNull()} $default $key COMMENT '${field.column.comment}';"
                    db.use { prepareStatement(sql) }
                } else {
                    var updateFlag = false
                    val b = ("NOT NULL" == field.getNotNull()) == ("NO" == find["IS_NULLABLE"])
                    if (b.not()) updateFlag = true
//                    if (find["DATA_TYPE"] != field.column.dbType) updateFlag = true
//                    if (field.column.length != 0L && find["CHARACTER_MAXIMUM_LENGTH"] != field.column.length)
//                        updateFlag = true
                    if (find["COLUMN_TYPE"] != field.getType())
                        updateFlag = true
                    if (find.getOrDefault("COLUMN_DEFAULT", "") != field.column.defaultVal
                        && "'" + find.getOrDefault("COLUMN_DEFAULT", "") + "'" != field.column.defaultVal
                    )
                        updateFlag = true
                    if (find["COLUMN_COMMENT"] != field.column.comment)
                        updateFlag = true
                    if ((find["EXTRA"] == "auto_increment") != (field.primaryKey?.autoIncrement == IncrementStrategy.AUTO))
                        updateFlag = true

                    if (updateFlag) {
                        val auto = getAuto(field)
                        val default = getDefault(field)
                        val sql =
                            "ALTER TABLE $fullName MODIFY COLUMN `$name` ${field.getType()} ${field.getNotNull()} $default $auto COMMENT '${field.column.comment}';"
                        db.use { prepareStatement(sql) }
                    }
                }
            }

            /*update index*/
            // TODO: 2022/6/2
        }
    }

    private fun getAuto(field: Field) =
        if (field.primaryKey != null) {
            if (field.primaryKey.autoIncrement == IncrementStrategy.AUTO) "auto_increment" else ""
        } else ""

    private fun getPK(field: Field) =
        if (field.primaryKey != null) {
            if (field.primaryKey.autoIncrement == IncrementStrategy.AUTO) "auto_increment primary key" else "primary key"
        } else ""

    private fun getDefault(field: Field) =
        if (field.column.defaultVal != "") "default ${field.column.defaultVal}" else ""
}