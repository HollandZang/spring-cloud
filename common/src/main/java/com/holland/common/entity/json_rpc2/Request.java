package com.holland.common.entity.json_rpc2;

@SuppressWarnings("SpellCheckingInspection")
public class Request {
    public final String jsonrpc = "2.0";
    public final String id;
    public final String method;
    public final Object params;

    public Request(String method) {
        this.id = "0";
        this.method = method;
        this.params = new Object[]{};
    }

    public Request(String method, Object params) {
        this.id = "0";
        this.method = method;
        this.params = params;
    }

    public Request(int id, String method) {
        this.id = String.valueOf(id);
        this.method = method;
        this.params = new Object[]{};
    }

    public Request(int id, String method, Object params) {
        this.id = String.valueOf(id);
        this.method = method;
        this.params = params;
    }
}
