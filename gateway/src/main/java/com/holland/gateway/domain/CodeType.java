package com.holland.gateway.domain;

import java.io.Serializable;

/**
 * 
 * @TableName code_type
 */
public class CodeType implements Serializable {
    /**
     * 
     */
    private String id;

    /**
     * 
     */
    private String des;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getDes() {
        return des;
    }

    /**
     * 
     */
    public void setDes(String des) {
        this.des = des;
    }
}