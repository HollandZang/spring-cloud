package com.holland

import com.mysql.cj.jdbc.ClientPreparedStatement
import java.sql.*

class JDBCConnectionPool(
    val dataSource: DataSource,
    host: String,
    port: String,
    private val user: String,
    private val pwd: String,
    database: String,
) : ObjectPool<Connection>() {

    private val url: String

    init {
        dataSource.loadDriver()
        this.url = dataSource.connectUrl(host, port, database)
    }

    override fun create(): Connection {
        return DriverManager.getConnection(url, user, pwd)
    }

    override fun validate(o: Connection?): Boolean {
        return o?.isClosed!!.not()
    }

    override fun expire(o: Connection?) {
        return o?.close()!!
    }

    inline fun <T> use(
        getResult: Connection.() -> PreparedStatement,
        useResult: ResultSet.() -> T,
    ): T? {
        checkOut().run {
            var statement: PreparedStatement? = null
            try {
                statement = getResult.invoke(this)
                if (printIt) println(
                    "> sql: $statement"
                )
                try {
                    statement.execute()
                } catch (e: SQLSyntaxErrorException) {
                    throw SQLSyntaxErrorException(statement.toString(), e)
                }
                val resultSet = statement.resultSet
                return useResult.invoke(resultSet)
            } finally {
                try {
                    statement?.resultSet?.close()
                } finally {
                    try {
                        statement?.close()
                    } finally {
                    }
                }
                checkIn(this)
            }
        }
    }

    inline fun use(
        getResult: Connection.() -> PreparedStatement,
    ) {
        with(checkOut()) {
            var statement: PreparedStatement? = null
            try {
                statement = getResult.invoke(this)
                if (printIt) println(
                    "> sql: $statement"
                )
                try {
                    val row = statement.executeUpdate()
                    if (printIt) println("> affected rows: $row")
                    row
                } catch (e: SQLSyntaxErrorException) {
                    throw SQLSyntaxErrorException(statement.toString(), e)
                }
            } finally {
                try {
                    statement?.close()
                } finally {
                }
                checkIn(this)
            }
        }
    }

    companion object {
        const val printIt: Boolean = true
    }
}