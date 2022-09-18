package com.holland.common.utils;


public class ValidatorMysql implements Validator {

    public final Object field;
    public final String fieldName;

    public ValidatorMysql(Object field, String fieldName) {
        this.field = field;
        this.fieldName = fieldName;
    }

    @Override
    public Validator notEmpty() {
        if (field == null || field.toString().isEmpty()) {
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
            if (((String) field).length() > maxLen) {
                ParameterException.accept("字段[%s]长度不能超过[%s]个字节", fieldName, maxLen);
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
            if (String.valueOf(field).length() <= minLen)
                ParameterException.accept("字段[%s]长度不能少于[%s]个字节", fieldName, minLen);
        }
        return this;
    }
}
