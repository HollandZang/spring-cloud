package com.holland.common.utils;


import com.alibaba.fastjson.JSON;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class XmlX {
    public static void main(String[] args) {
        final File file = new File("D:\\practise\\spring-cloud\\pom.xml");
        String s;
        try (FileInputStream is = new FileInputStream(file)) {
            s = new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Document document = parseXml(s);
        final Element element = document.getDocumentElement();

        int i = 0;
        format2List(element, i);
    }

    static void format2List(Node element, int layer) {
        final List<String> list = new ArrayList<>();
        format2List(element, layer, "", list);

        final Map<String, Object> map = new HashMap<>();
        for (String prop : list) {
            final String[] strings = prop.split(splitKey);

            final List<String> others = list.stream().filter(l -> !prop.equals(l)).collect(Collectors.toList());
            StringBuilder path = new StringBuilder();
            Map<String, Object> inner = map;
            for (int i = 0; i < strings.length; i++) {
                final String key = strings[i];
                if (i == strings.length - 2) {
                    inner.put(key, strings[i + 1]);
                    break;
                }

                path.append(key + splitKey + strings[i + 1]);
                final Optional<String> any = others.stream().filter(l -> l.startsWith(path.toString())).findAny();
                if (any.isEmpty()) {
                    Object o = inner.get(key);
                    if (o == null) {
                        o = new HashMap<>();
                        inner.put(key, o);
                    }
                    inner = (Map<String, Object>) o;
                } else {
//                    List<Map> o = (List<Map>) inner.get(key);
//                    Map<String, Object> m = new HashMap<>();
//                    if (o == null)
//                        o = new ArrayList<>();
//                    o.add(m);
//                    inner.put(key, o);
//                    inner = m;
                }
            }
        }
        System.out.println(JSON.toJSONString(map));
    }

    static String splitKey = "/_/";

    static void format2List(Node element, int layer, String path, List<String> list) {
        final String nodeName = element.getNodeName();
        final String pName = element.getParentNode().getNodeName();
        final String textContent = element.getTextContent();

        if ("#comment".equals(nodeName)) return;
        if (textContent.isBlank()) return;

        if (element.hasChildNodes()) {
//            System.out.println("  ".repeat(Math.max(0, layer)) + nodeName);
            final NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node node = childNodes.item(i);
                format2List(node, layer + 1, path + nodeName + splitKey, list);
            }
        } else {
            System.out.println(path + textContent);
            list.add(path + textContent);
        }
    }

    public final Document resource;

    public XmlX(Document resource) {
        this.resource = resource;
    }

    public <T> T find(String expression) {
        return null;
    }

    public static Document parseXml(String s) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);

        final DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Try parse 'xml' error when init 'DocumentBuilder'");
        }

        try (final ByteArrayInputStream input = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8))) {
            return builder.parse(input);
        } catch (SAXException e) {
            throw new RuntimeException("Try parse 'xml' error when translate it");
        } catch (IOException e) {
            throw new RuntimeException("Try parse 'xml' error when assemble object");
        }
    }

}
