package com.holland.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.With;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

@Data
@TableName(autoResultMap = true)
public class Miniapp {
    @TableId(value = "appid", type = IdType.AUTO)
    public final String appid;
    public final String secret;
    public final String name;

    @TableField(typeHandler = FastjsonTypeHandler.class,javaType = true)
    public final CodeType jsonExtra;
}
