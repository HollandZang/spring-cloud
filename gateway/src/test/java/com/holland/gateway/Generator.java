package com.holland.gateway;

import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import com.holland.common.enums.gateway.CodeTypeEnum;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.gateway.mapper.CodeTypeMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Generator {

    @Resource
    CodeMapper codeMapper;
    @Resource
    CodeTypeMapper codeTypeMapper;

    String module_common = "../common/src/main/java/";
    String resource = "./src/test/resources/";

    @Test
    void genCodeTypeEnum() throws IOException, TemplateException {
        final String path_java = "com.holland.common.enums.gateway";
        final File file = new File(module_common + path_java.replaceAll("\\.", "/") + "/CodeTypeEnum.java");
        final FileWriter writer = new FileWriter(file);

        final String collect = codeTypeMapper.selectList(null).stream()
                .map(CodeType::getId)
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));

        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDirectoryForTemplateLoading(new File(resource));
        configuration.setDefaultEncoding("UTF-8");

        final Template template = configuration.getTemplate("Enum.ftl");
        final HashMap<Object, Object> data = new HashMap<>();
        data.put("package", path_java);
        data.put("class", "CodeTypeEnum");
        data.put("valueStr", collect + ";");
        template.process(data, writer);
        writer.close();
    }

    @Test
    void genRoleEnum() throws IOException, TemplateException {
        final String path_java = "com.holland.common.enums.gateway";
        final File file = new File(module_common + path_java.replaceAll("\\.", "/") + "/RoleEnum.java");
        final FileWriter writer = new FileWriter(file);

        final String collect = codeMapper.getByCode_type_id(CodeTypeEnum.ROLE).stream()
                .map(Code::getVal)
                .map(String::toUpperCase)
                .collect(Collectors.joining(",", "TOKEN,", ";"));

        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDirectoryForTemplateLoading(new File(resource));
        configuration.setDefaultEncoding("UTF-8");

        final Template template = configuration.getTemplate("Enum.ftl");
        final HashMap<Object, Object> data = new HashMap<>();
        data.put("package", path_java);
        data.put("class", "RoleEnum");
        data.put("valueStr", collect);
        template.process(data, writer);
        writer.close();
    }
}
