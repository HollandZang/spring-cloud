package com.holland.util

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object XmlUtil {

    fun readXml(path: String, fileName: String): Document? {
        val xml = FileUtil.readFile(path, fileName)
        return xml2Doc(xml)
    }

    fun xml2Doc(xml: String): Document? {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isIgnoringElementContentWhitespace = true
        val builder: DocumentBuilder = try {
            factory.newDocumentBuilder()
        } catch (e: ParserConfigurationException) {
            System.err.println("Try parse 'xml' error when init 'DocumentBuilder'")
            e.printStackTrace()
            return null
        }
        val input = ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8))
        val doc: Document = try {
            builder.parse(input)
        } catch (e: SAXException) {
            System.err.println("Try parse 'xml' error when translate it")
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            System.err.println("Try parse 'xml' error when assemble object")
            e.printStackTrace()
            return null
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return doc
    }
}

fun main() {
    val doc = XmlUtil.readXml("conf\\", "test.xml")
    val root: Element = doc!!.documentElement

    val nodeValue = root.getElementsByTagName("tagOne")
        .item(0)
        .firstChild
        .nodeValue
    println(nodeValue)
}