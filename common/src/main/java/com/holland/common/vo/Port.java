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
public class Port {

    public Integer value;

    public boolean validate() {
        return value.toString().matches("^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$");
    }

    static {
        PortSerializer serializer = new PortSerializer();
        SerializeConfig.globalInstance.put(Port.class, serializer);
        ParserConfig.global.putDeserializer(Port.class, serializer);
    }

    public static class PortSerializer implements ObjectSerializer, ObjectDeserializer {

        @SuppressWarnings("unchecked")
        @Override
        public Port deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Object parse = parser.parse();
            return new Port((Integer) parse);
        }

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            SerializeWriter out = serializer.getWriter();
            out.write('"' + ((Port) object).value + '"');
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
