package com.holland.util

import java.io.File

object FileUtil {

    fun mkdir(path: String) {
        val file = File(path)
        when (file.exists()) {
//            true -> println("exists directory: $path")
            false -> {
                if (!file.mkdir()) {
                    mkdir(path.substring(0, path.lastIndexOf(File.separatorChar)))
                    file.mkdir()
                }
                println("create directory: $path")
            }
            else -> {}
        }
    }

    fun append2File(content: String?, path: String, fileName: String) {
        mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        when (file.exists()) {
            true -> println("append file: ${file.path}")
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
            }
        }
        content?.let { file.appendText(it + System.getProperty("line.separator")) }
    }

    fun newFile(content: String?, path: String, fileName: String) {
        mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        when (file.exists()) {
            true -> println("rewrite file: ${file.path}")
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
            }
        }
        content?.let { file.writeText(it + System.getProperty("line.separator")) }
    }

    fun readFile(path: String, fileName: String): String {
        val file = File("$path${File.separatorChar}$fileName")
        return file.readText()
    }

    fun readFile4Line(path: String, fileName: String): Array<String> {
        val file = File("$path${File.separatorChar}$fileName")
        return if (file.exists()) {
            file.readLines().toTypedArray()
        } else arrayOf()
    }

    fun readDir(path: String): Array<out String> {
        val file = File(path)
        return if (file.exists() && file.isDirectory) {
            file.list() ?: arrayOf()
        } else arrayOf()
    }
}