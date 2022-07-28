package com.holland.common.utils;

import java.util.Arrays;

public class Comm {
    public static char DEFAULT_LOCK_SPLIT = ':';

    public static String concatStr(String... key) {
        return Arrays.stream(key)
                .reduce((s, s2) -> s + DEFAULT_LOCK_SPLIT + s2)
                .orElse("null");
    }
}
