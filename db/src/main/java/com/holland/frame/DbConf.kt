package com.holland.frame

import com.holland.JDBCConnectionPool

object DbConf {
    val m: Map<String, JDBCConnectionPool> = mutableMapOf()
}