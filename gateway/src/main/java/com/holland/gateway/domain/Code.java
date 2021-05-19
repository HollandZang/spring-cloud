package com.holland.gateway.domain;

import java.io.Serializable;

/**
 * 
 * @TableName code
 */
public class Code implements Serializable {
    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private String code;

    /**
     * 
     */
    private String name;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 
     */
    public void setName(String name) {
        this.name = name;
    }
}