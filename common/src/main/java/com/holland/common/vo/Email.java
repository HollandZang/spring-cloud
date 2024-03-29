package com.holland.common.vo;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

@NoArgsConstructor
@AllArgsConstructor
public class Email {

    public String value;

    public boolean validate() {
        return value.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    static {
        EmailSerializer serializer = new EmailSerializer();
        SerializeConfig.globalInstance.put(Email.class, serializer);
        ParserConfig.global.putDeserializer(Email.class, serializer);
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

    @Override
    public String toString() {
        return value;
    }
}
