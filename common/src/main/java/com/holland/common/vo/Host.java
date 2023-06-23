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
public class Host {

    public String value;

    public boolean validate() {
        return validateDomain() && validateIP();
    }

    public boolean validateDomain() {
        return value.matches("^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$");
    }

    public boolean validateIP() {
        return value.matches("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
    }

    static {
        HostSerializer serializer = new HostSerializer();
        SerializeConfig.globalInstance.put(Host.class, serializer);
        ParserConfig.global.putDeserializer(Host.class, serializer);
    }

    public static class HostSerializer implements ObjectSerializer, ObjectDeserializer {

        @SuppressWarnings("unchecked")
        @Override
        public Host deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Object parse = parser.parse();
            return new Host((String) parse);
        }

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            SerializeWriter out = serializer.getWriter();
            out.write('"' + ((Host) object).value + '"');
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
