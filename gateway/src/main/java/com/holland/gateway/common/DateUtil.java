package com.holland.gateway.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(new Date());
    }

    public static String getDateStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }
}
