package com.holland.common.vo;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.holland.common.utils.StrEnhance;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Type;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Email implements StrEnhance {

    public String value;

    public boolean validate() {
        return value.matches("^[A-Za-z\\d-_.\\u4e00-\\u9fa5]+@[a-zA-Z\\d_-]+(\\.[a-zA-Z\\d_-]+)+$");
    }

    static {
        EmailSerializer serializer = new EmailSerializer();
        SerializeConfig.globalInstance.put(Email.class, serializer);
        ParserConfig.global.putDeserializer(Email.class, serializer);
    }

    @Override
    public String getVal() {
        return value;
    }

    public static class EmailSerializer implements ObjectSerializer, ObjectDeserializer {

        @SuppressWarnings("unchecked")
        @Override
        public Email deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Object parse = parser.parse();
            return new Email((String) parse);
        }

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            SerializeWriter out = serializer.getWriter();
            out.write('"' + ((Email) object).value + '"');
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
}
