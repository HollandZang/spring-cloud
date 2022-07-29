package com.holland.common.entity.gateway;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * @TableName code
 */
public class Code implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String code_type_id;

    private String des;

    private String val;

    private String val1;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public Code setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCode_type_id() {
        return code_type_id;
    }

    public Code setCode_type_id(String code_type_id) {
        this.code_type_id = code_type_id;
        return this;
    }

    public String getDes() {
        return des;
    }

    public Code setDes(String des) {
        this.des = des;
        return this;
    }

    public String getVal() {
        return val;
    }

    public Code setVal(String val) {
        this.val = val;
        return this;
    }

    public String getVal1() {
        return val1;
    }

    public Code setVal1(String val1) {
        this.val1 = val1;
        return this;
    }
}