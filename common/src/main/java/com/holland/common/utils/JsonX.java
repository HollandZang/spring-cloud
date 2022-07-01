package com.holland.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.holland.common.utils.CommX.actions;

public class JsonX {
    public static void main(String[] args) {
        JsonX.expression("m'data[0]+i'data[0].uid-f'data[0].activeCid");

        final JsonX jsonX = new JsonX(json);
        JSONObject l = jsonX.find("data[0]");
        System.out.println(l);
    }

    public static <T> T expression(String expression) {
        final String[] split = expression.split(Arrays.toString(actions));
        final List<Block> blocks = Arrays.stream(split)
                .map(Block::new)
                .collect(Collectors.toList());
        final Map a = blocks.get(0).parseActions(json);
        final Integer b = blocks.get(1).parseActions(json);
        final Float c = blocks.get(2).parseActions(json);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        return null;
    }

    public final JSON resource;

    public JsonX(JSON resource) {
        this.resource = resource;
    }

    public <T> T find(String expression) {
        return new Block(expression).parseActions(resource);
    }

    static class Block {
        public final CommX.Types type;
        public final String word;

        Block(String s) {
            final String[] pair = s.split("'");
            if (pair.length == 1) {
                this.type = null;
                this.word = pair[0];
            } else {
                this.type = CommX.Types.valueOf(pair[0]);
                this.word = pair[1];
            }
        }

        Object convert(Object o) {
            if (o == null) return null;
            if (type == null) return o;
            switch (type) {
                case i32:
                case i:
                    return Integer.parseInt(o.toString());
                case i64:
                    return Long.parseLong(o.toString());
                case f32:
                case f:
                    return Float.parseFloat(o.toString());
                case f64:
                case d:
                    return Double.parseDouble(o.toString());
                case bool:
                case b:
                    return Boolean.parseBoolean(o.toString());
                case chr:
                case c:
                    return o.toString().charAt(0);
                case str:
                case s:
                    return o.toString();
                case list:
                case l:
                    return ((JSONArray) o).toJavaList(Object.class);
                case map:
                case m:
                    return ((JSONObject) o).getInnerMap();
            }
            return o;
        }

        BiFunction<Object, String, Object> supplierObj = (J, key) -> {
            final JSONObject j = (JSONObject) J;
            return j.get(key);
        };

        BiFunction<Object, String, Object> supplierArr = (J, index) -> {
            final JSONArray j = (JSONArray) J;
            return j.get(Integer.parseInt(index));
        };

        <T> T parseActions(JSON json) {
            final List<BiFunction<Object, String, Object>> actions = new ArrayList<>();
            final List<String> list = new ArrayList<>();
            final char[] chars = word.toCharArray();
            final StringBuilder action = new StringBuilder();
            boolean arrFlag = false;
            for (int i = 0; i < chars.length; i++) {
                final char c = chars[i];
                if (c == '[' && i > 0) {
                    if (action.length() == 0) throw new RuntimeException();
                    arrFlag = true;
                    actions.add(supplierObj);
                    list.add(action.toString());
                    action.delete(0, action.length());
                    continue;
                }
                if (c == ']') {
                    if (action.length() == 0) throw new RuntimeException();
                    if (!arrFlag) throw new RuntimeException();
                    final String index = action.toString();
                    if (false) throw new RuntimeException();
                    arrFlag = false;
                    actions.add(supplierArr);
                    list.add(index);
                    action.delete(0, action.length());
                    continue;
                }
                if (c == '.') {
                    if (action.length() == 0) {
                        if (chars[i - 1] == ']') continue;
                        throw new RuntimeException();
                    }
                    if (arrFlag) throw new RuntimeException();
                    actions.add(supplierObj);
                    list.add(action.toString());
                    action.delete(0, action.length());
                    continue;
                }
                action.append(c);
                if (i == chars.length - 1) {
                    if (arrFlag) throw new RuntimeException();
                    actions.add(supplierObj);
                    list.add(action.toString());
                }
            }

            Object res = json;
            for (int i = 0; i < actions.size(); i++) {
                res = actions.get(i).apply(res, list.get(i));
            }

            return (T) convert(res);
        }
    }

    static final JSON json = (JSON) JSON.parse("{a:1,data:[{uid:\"157580\",activeCid:\"69\",activeChannel:\"kuaikan\",activeGid:\"1000\",activeIP:\"2882823743\",channelUid:\"91627840_3\",status:\"0\"}]}");
}