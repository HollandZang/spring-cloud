package com.holland.common.plugin.mybaties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MyPageHelper {
    private final int pageNum;
    private final int pageSize;

    public static void startPage(Integer pageNum, Integer pageSize) {
        MyPageInterceptor.helper.set(new MyPageHelper(setDefault(pageNum, 1), setDefault(pageSize, 10)));
    }

    private static int checkNull(Integer val, Integer defaultVal) {
        return val == null ? defaultVal : val;
    }

    private static int setDefault(Integer val, Integer defaultVal) {
        val = checkNull(val, defaultVal);
        return val <= 0 ? defaultVal : val;
    }
}
