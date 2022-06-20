package com.holland.util

import java.util.regex.Pattern

object RegUtil {

    fun hostCheck(host: String) =
        Pattern.matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(localhost)$", host)

    fun portCheck(port: String) =
        Pattern.matches("^\\d{1,5}$", port)

}
