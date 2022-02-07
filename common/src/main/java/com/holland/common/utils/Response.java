package com.holland.common.utils;

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
        return success(null);
    }

    public static <T> Response<T> success(T data) {
        return success(data, 0L);
    }

    public static <T> Response<T> success(T data, Long count) {
        return new Response<T>(200, "", data, count);
    }

    public static <T> Response<T> failed(Exception e) {
        return new Response<T>(500, e.getClass().getName() + "::" + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()), null, 0);
    }

    public static <T> Response<T> failed(String msg) {
        return new Response<T>(500, msg, null, 0);
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
