package com.holland.util

import com.google.gson.Gson
import java.sql.ResultSet

object DbUtil {

    /**
     * @param resultSet     resultSet from sql query
     * @param mappingFrom   what kind of database naming rule
     * @param mappingTo     what kind of pojo naming rule
     * @param resultMap     convert map, database name to pojo name
     * @param clazz         Need NoArgsConstructor
     */
    @JvmOverloads
    fun <T> getResult(
        resultSet: ResultSet,
        mappingFrom: CaseFormat? = null,
        mappingTo: CaseFormat? = null,
        resultMap: Map<String, String>? = null,
        clazz: Class<T>,
    ): List<T> {
        val result = mutableListOf<T>()
        while (resultSet.next()) {
//            val item = clazz.getConstructor().newInstance()
            val map = mutableMapOf<String, Any>()
            for (i in 1..resultSet.metaData.columnCount) {
                val dbName = resultSet.metaData.getColumnName(i)
                val columnName =
                    if (resultMap != null && resultMap.containsKey(dbName)) {
                        resultMap[dbName]
                    } else {
                        if (mappingFrom == null || mappingTo == null) dbName
                        else mappingFrom.to(mappingTo, dbName)
                    }!!
//                反射会因为字段类型不一致而set失败
//                clazz.getDeclaredField(columnName).run {
//                    isAccessible = true
//                    set(item, resultSet.getObject(i))
//                }
                val v = resultSet.getObject(i)
                if (v != null)
                    map[columnName] = v
            }
            Gson().run {
                val str = toJson(map)
                fromJson(str, clazz)
            }.also { result.add(it) }
        }
        return result.toList()
    }

    /**
     * @param resultSet     resultSet from sql query
     */
    fun getResult(
        resultSet: ResultSet,
    ): List<Map<String, *>> {
        val result = mutableListOf<Map<String, *>>()
        while (resultSet.next()) {
            val item = mutableMapOf<String, Any>()
            for (i in 1..resultSet.metaData.columnCount) {
                val columnName = resultSet.metaData.getColumnName(i)
                val v = resultSet.getObject(i)
                if (v != null)
                    item[columnName] = v
            }
            result.add(item)
        }
        return result.toList()
    }
}