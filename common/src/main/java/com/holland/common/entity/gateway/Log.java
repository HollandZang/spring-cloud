package com.holland.common.entity.gateway;

import com.holland.common.utils.DateUtil;

import java.io.Serializable;
import java.util.Date;

public class Log implements Serializable {
    private String operateUser;
    private long timestamp;
    private String date;
    private String reqLine;
    private String ip;
    private String param;
    private String body;

    private Integer resCode;
    private String resData;

    private static final long serialVersionUID = 1L;

    public String getBody() {
        return body;
    }

    public Log setBody(String body) {
        this.body = body;
        return this;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public Log setOperateUser(String operateUser) {
        this.operateUser = operateUser;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Log setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        final Date d = new Date(timestamp);
        this.date = DateUtil.getDateStr(d);
        return this;
    }

    public String getDate() {
        return date;
    }

    public String getReqLine() {
        return reqLine;
    }

    public Log setReqLine(String reqLine) {
        this.reqLine = reqLine;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Log setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getParam() {
        return param;
    }

    public Log setParam(String param) {
        this.param = param;
        return this;
    }

    public Integer getResCode() {
        return resCode;
    }

    public Log setResCode(Integer resCode) {
        this.resCode = resCode;
        return this;
    }

    public String getResData() {
        return resData;
    }

    public Log setResData(String resData) {
        this.resData = resData;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}