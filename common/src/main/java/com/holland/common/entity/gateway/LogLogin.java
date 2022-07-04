package com.holland.common.entity.gateway;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志表
 * @TableName log_login
 */
public class LogLogin implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 操作人
     */
    private String operate_user;

    /**
     * 操作时间
     */
    private Date operate_time;

    /**
     * 操作类型 1: 登录  0: 登出
     */
    private String operate_type;

    /**
     * 指明通过什么软件、项目登录
     */
    @TableField("'from'")
    private String from;

    /**
     * ip来源
     */
    private String ip;

    /**
     * 
     */
    private Integer result;

    /**
     * 
     */
    private String response;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public LogLogin setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getOperate_user() {
        return operate_user;
    }

    public LogLogin setOperate_user(String operate_user) {
        this.operate_user = operate_user;
        return this;
    }

    public Date getOperate_time() {
        return operate_time;
    }

    public LogLogin setOperate_time(Date operate_time) {
        this.operate_time = operate_time;
        return this;
    }

    public String getOperate_type() {
        return operate_type;
    }

    public LogLogin setOperate_type(String operate_type) {
        this.operate_type = operate_type;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public LogLogin setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public LogLogin setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getResult() {
        return result;
    }

    public LogLogin setResult(Integer result) {
        this.result = result;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public LogLogin setResponse(String response) {
        this.response = response;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}