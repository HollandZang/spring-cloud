package com.holland.common.entity.gateway;

import com.holland.common.utils.DateUtil;

import java.io.Serializable;
import java.util.Date;

public class LogLogin implements Serializable {
    private String loginName;
    private String pwd;
    private long timestamp;
    private String date;
    private String actionType;
    private String from;
    private String ip;
    private Integer resCode;
    private String resBody;

    private static final long serialVersionUID = 1L;

    public String getPwd() {
        return pwd;
    }

    public LogLogin setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public LogLogin setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogLogin setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        final Date d = new Date(timestamp);
        this.date = DateUtil.getDateStr(d);
        return this;
    }

    public String getDate() {
        return date;
    }

    public String getActionType() {
        return actionType;
    }

    public LogLogin setActionType(String actionType) {
        this.actionType = actionType;
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

    public Integer getResCode() {
        return resCode;
    }

    public LogLogin setResCode(Integer resCode) {
        this.resCode = resCode;
        return this;
    }

    public String getResBody() {
        return resBody;
    }

    public LogLogin setResBody(String resBody) {
        this.resBody = resBody;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}