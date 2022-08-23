package com.holland.common.utils;


public class ValidatorOracle implements Validator {

    public final Object field;
    public final String fieldName;

    public ValidatorOracle(Object field, String fieldName) {
        this.field = field;
        this.fieldName = fieldName;
    }

    @Override
    public Validator notEmpty() {
        if (field == null || field.toString().isBlank()) {
            ParameterException.accept("字段[%s]不能为空", fieldName);
        }
        return this;
    }

    @Override
    public Validator lenLT(int maxLen) {
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

    @Override
    public Validator lenGE(int minLen) {
        if (field == null) {
            return this;
        }
        if (field instanceof Number) {
            if (String.valueOf(field).length() <= minLen) {
                ParameterException.accept("字段[%s]长度不能少于[%s]个字节", fieldName, minLen);
            }
        }

        if (field instanceof String) {
            int len = 0;
            boolean flag = true;
            for (char c : ((String) this.field).toCharArray()) {
                len += c > 127 || c == 97 ? 2 : 1;
                if (len >= minLen) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                ParameterException.accept("字段[%s]长度不能少于[%s]个字节", fieldName, minLen);
        }
        return this;
    }
}
