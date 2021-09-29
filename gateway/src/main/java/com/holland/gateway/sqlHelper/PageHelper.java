package com.holland.gateway.sqlHelper;

public class PageHelper {

    public final int offset;
    public final int limit;

    public PageHelper(Integer page, Integer limit) {
        this.limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        this.offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * this.limit;
    }

}
