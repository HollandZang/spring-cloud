package com.holland

import com.holland.frame.annotation.Table

enum class DataSource(val upperCamelCase: String, val lowerCase: String) {
    ORACLE("Oracle", "oracle") {
        override fun loadDriver() {
            Class.forName("oracle.jdbc.driver.OracleDriver")
        }

        override fun connectUrl(host: String, port: String, database: String) =
            "jdbc:oracle:thin:@${host}:${port}/${database}"

        override fun tableFullName(table: Table, tableName: String): String =
            "\"${table.db}\".\"$tableName\""

        override fun quoteName(name: String): String {
            TODO("Not yet implemented")
        }

    },
    MYSQL("Mysql", "mysql") {
        override fun loadDriver() {
            Class.forName("com.mysql.cj.jdbc.Driver")
        }

        override fun connectUrl(host: String, port: String, database: String) =
            "jdbc:mysql://${host}:${port}/$database?useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&serverTimezone=Asia/Shanghai"

        override fun tableFullName(table: Table, tableName: String): String =
            "`${table.db}`.`$tableName`"

        override fun quoteName(name: String): String =
            "`$name`"
    },
    POSTGRES("Postgres", "postgres") {
        override fun loadDriver() {
            Class.forName("org.postgresql.Driver")
        }

        override fun connectUrl(host: String, port: String, database: String) =
            "jdbc:postgresql://${host}:${port}/${database}"

        override fun tableFullName(table: Table, tableName: String): String =
            "\"${table.db}\".\"${table.schema}\".\"$tableName\""

        override fun quoteName(name: String): String =
            "\"$name\""
    },
    ;

    abstract fun loadDriver()
    abstract fun connectUrl(host: String, port: String, database: String): String
    abstract fun tableFullName(table: Table, tableName: String): String
    abstract fun quoteName(name: String): String

    companion object {
        infix fun getEnum(d: String): DataSource {
            return values().find { it.lowerCase == d.toLowerCase() }
                ?: kotlin.run {
                    throw EnumConstantNotPresentException(DataSource::class.java, d)
                }
        }
    }
}