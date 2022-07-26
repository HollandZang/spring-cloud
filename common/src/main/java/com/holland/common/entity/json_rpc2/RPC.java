package com.holland.common.entity.json_rpc2;

@SuppressWarnings("SpellCheckingInspection")
public class RPC {

    public static class Success<T> extends RPC {
        public final String jsonrpc;
        public final String id;
        public final T result;

        public Success(String id, T result) {
            this.jsonrpc = "2.0";
            this.id = id;
            this.result = result;
        }
    }

    public static class Error extends RPC {
        public final String jsonrpc;
        public final String id;
        public final ErrorInfo error;

        public Error(String id, int code, String message) {
            this.jsonrpc = "2.0";
            this.id = id;
            this.error = new ErrorInfo(code, message);
        }
    }

    private static class ErrorInfo {
        public final int code;
        public final String message;

        public ErrorInfo(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
