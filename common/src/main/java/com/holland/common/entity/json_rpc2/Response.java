package com.holland.common.entity.json_rpc2;

@SuppressWarnings("SpellCheckingInspection")
public class Response<T> {
    public final String jsonrpc;
    public final String id;

    public final T result;

    public final RpcError error;

    public Response(String jsonrpc, String id, T result, RpcError error) {
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.result = result;
        this.error = error;
    }

    public <R> R getResult() {
        //noinspection unchecked
        return (R) this.result;
    }

    public boolean failed() {
        return error != null;
    }

    public static class RpcError {
        public final int code;
        public final String message;

        public RpcError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return "{" +
                    "code:" + code +
                    ", message:'" + message + '\'' +
                    '}';
        }
    }
}
