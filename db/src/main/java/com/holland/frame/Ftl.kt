package com.holland.frame

import com.holland.DataSource
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.StringWriter
import java.io.Writer

object Ftl {
    private var configurations = mutableMapOf<DataSource, Configuration>()

    fun generate(dataSource: DataSource, ftlName: String, data: Any): String {
        val configuration = configurations[dataSource]
            ?: kotlin.run {
                Configuration(Configuration.VERSION_2_3_28)
                    .apply {
                        val path = this.javaClass.classLoader.getResource("./ftl/" + dataSource.lowerCase)!!.path
                        setDirectoryForTemplateLoading(File(path))
                        defaultEncoding = "utf-8"
                    }
                    .also { configurations[dataSource] = it }
            }

        val template: Template = configuration.getTemplate(ftlName)
        val out: Writer = StringWriter()
        template.process(data, out)
        out.close()
        return out.toString()
    }
}