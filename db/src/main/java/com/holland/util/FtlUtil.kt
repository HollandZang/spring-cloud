package com.holland.util

import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.FileWriter
import java.io.Writer

object FtlUtil {

    private lateinit var configuration: Configuration

    fun generate(data: Any, ftlName: String, outPath: String, outName: String) {
        if (this::configuration.isInitialized.not()) {
            configuration = Configuration(Configuration.VERSION_2_3_28)
            configuration.setDirectoryForTemplateLoading(File("./conf/ftl"))
            configuration.defaultEncoding = "utf-8"
        }

        FileUtil.mkdir(outPath)

        val template: Template = configuration.getTemplate(ftlName)
        val out: Writer = FileWriter(outPath + File.separatorChar + outName)
        template.process(data, out)
        out.close()

        println("create file: ${outPath + File.separatorChar + outName}")
    }

}