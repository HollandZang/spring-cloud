package com.holland.common.utils;

public class Files {
    public static String extension(String name) {
        final int i = name.lastIndexOf('.');
        return i == -1 ? "" : name.substring(i + 1);
    }
}
