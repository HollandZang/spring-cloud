package com.holland.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response<T> {
    public final int code;
    public final String msg;
    public final T data;
    public final long count;

    private Response(int code, String msg, T data, long count) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.count = count;
    }

    public static <T> Response<T> success() {
        return success(null, 0L);
    }

    public static <T> Response<T> success(T data) {
        return success(data, 0L);
    }

    public static <T> Response<List<T>> success(Page<T> page) {
        return new Response<>(200, "", page.getRecords(), page.getTotal());
    }

    public static <T> Response<T> success(T data, Long count) {
        return new Response<>(200, "", data, count);
    }

    public static <T> Response<T> later() {
        return new Response<>(1, "please later", null, 0);
    }

    public static <T> Response<T> failed(Throwable e) {
        return new Response<>(500, e.getClass().getName() + "::" + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()), null, 0);
    }

    public static <T> Response<T> existErr(String name, Object... args) {
        return failed(name + "已存在", args);
    }

    public static <T> Response<T> notExistErr(String name, Object... args) {
        return failed(name + "不存在", args);
    }

    private static final Pattern LOGGER_COMPILE = Pattern.compile("\\{}");

    public static <T> Response<T> failed(String msg, Object... args) {
        if (args.length == 0) return new Response<>(500, msg, null, 0);

        final StringBuilder newMsg = new StringBuilder();
        final Matcher matcher = LOGGER_COMPILE.matcher(msg);
        int end = 0;
        for (Object arg : args) {
            if (matcher.find()) {
                newMsg.append(msg, end, matcher.start())
                        .append(arg);
                end = matcher.end();
            } else {
                break;
            }
        }
        if (end < msg.length()) newMsg.append(msg.substring(end));
        return new Response<>(500, newMsg.toString(), null, 0);
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", count=" + count +
                '}';
    }
}
