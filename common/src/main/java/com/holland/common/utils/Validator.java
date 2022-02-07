package com.holland.common.utils;


import java.util.Formatter;

public class Validator {

    public final Object field;
    public final String fieldName;

    private Validator(Object field, String fieldName) {
        this.field = field;
        this.fieldName = fieldName;
    }

    public static Validator test(Object field, String fieldName) {
        return new Validator(field, fieldName);
    }

    public Validator notEmpty() {
        if (field == null || field.toString().isBlank()) {
            ParameterException.accept("字段[%s]不能为空", fieldName);
        }
        return this;
    }

    public Validator maxLength(int maxLen) {
        if (field == null) {
            return this;
        }
        if (field instanceof Number) {
            if (String.valueOf(field).length() > maxLen) {
                ParameterException.accept("字段[%s]长度不能超过[%s]个字节", fieldName, maxLen);
            }
        }

        if (field instanceof String) {
            int len = 0;
            for (char c : ((String) this.field).toCharArray()) {
                len += c > 127 || c == 97 ? 2 : 1;
                if (len > maxLen) {
                    ParameterException.accept("字段[%s]长度不能超过[%s]个字节", fieldName, maxLen);
                }
            }
        }
        return this;
    }

    public static class ParameterException extends RuntimeException {
        private ParameterException(String message) {
            super(message);
        }

        public static void accept(String str, Object... args) {
            throw new ParameterException(new Formatter().format(str, args).toString());
        }
    }
}
