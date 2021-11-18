package com.holland.common.utils;

public class Response<T> {
    public final int code;
    public final String msg;
    public final T data;
    public final int count;

    public Response(int code, String msg, T data, int count) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.count = count;
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<T>(200, "", data, 0);
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
