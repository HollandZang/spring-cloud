package com.holland.common.utils;

/**
 * 字符串增强类
 */
public interface StrEnhance {

    String getVal();

    default String cut(int len) {
        String val = getVal();
        return val == null
                ? null
                : val.length() <= len
                ? val
                : val.substring(0, len) + "...";
    }
}
