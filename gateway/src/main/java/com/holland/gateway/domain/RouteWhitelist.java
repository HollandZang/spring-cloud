package com.holland.gateway.domain;

import java.io.Serializable;

/**
 * 路由白名单
 * @TableName route_whitelist
 */
public class RouteWhitelist implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private Boolean enabled;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}