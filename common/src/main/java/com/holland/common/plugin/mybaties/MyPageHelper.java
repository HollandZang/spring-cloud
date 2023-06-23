package com.holland.common.plugin.mybaties;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class MyPageHelper {
    private final long pageNum;
    private final long pageSize;

    public static void startPage(Long pageNum, Long pageSize) {
        MyPageInterceptor.helper.set(new MyPageHelper(setDefault(pageNum, 1L), setDefault(pageSize, 10L)));
    }

    private static long checkNull(Long val, Long defaultVal) {
        return val == null ? defaultVal : val;
    }

    private static long setDefault(Long val, Long defaultVal) {
        val = checkNull(val, defaultVal);
        return val <= 0 ? defaultVal : val;
    }

    public static void startPage(Page<Map<String, Object>> page) {
        MyPageInterceptor.helper.set(new MyPageHelper(setDefault(page.getCurrent(), 1L), setDefault(page.getSize(), 10L)));
    }
}
