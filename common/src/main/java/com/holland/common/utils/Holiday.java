package com.holland.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.holland.net.Net;
import com.holland.net.common.PairBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class Holiday {

    public static void main(String[] args) throws InterruptedException {
        CopyOnWriteArrayList<Object> list = new CopyOnWriteArrayList<>();
        Net net = new Net();

        // 未来时间的节假日只能找出节假日当天，不能知道具体放假的是哪几天，这个要临近节日时等国家通知
        int[] years = new int[]{2021, 2022, 2023, 2024, 2025};
        int[] months = new int[]{2, 5, 8, 11};
        CountDownLatch countDownLatch = new CountDownLatch(years.length * months.length);

        for (int year : years) {
            for (int month : months) {
                String query = year + "年" + month + "月";
                long l = System.currentTimeMillis();
                // 百度api 前后一月，共三月数据
                net.async.get("https://opendata.baidu.com/api.php"
                        , null
                        , new PairBuilder()
                                .add("tn", "wisetpl")
                                .add("format", "json")
                                .add("resource_id", 39043)
                                .add("query", query)
                                .add("t", l)
                                .add("cb", "op_aladdin_callback" + l)
                        , response -> {
                            try {
                                String pre = "/**/op_aladdin_callback" + l + "(";
                                String end = ");";
                                String string = response.body().string();
                                string = string.substring(pre.length(), string.length() - end.length());

                                JsonX jsonX = new JsonX(string);
                                JSONArray arr = jsonX.find("data[0]almanac");
                                for (Object obj : arr) {
                                    JsonX json = new JsonX(obj);
                                    if (new EvalX().exec("status=='1'", json)) {
                                        String key = json.find("year")
                                                + String.format("%02d", (Object) json.find("i'month"))
                                                + String.format("%02d", (Object) json.find("i'day"));
                                        list.add(key + "-" + json.find("term"));
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } finally {
                                countDownLatch.countDown();
                            }
                        });
            }
        }

        countDownLatch.await();
        list.sort(Comparator.comparing(j -> j.toString().substring(0, 8)));
        System.out.println(list);
        System.exit(1);
    }

}
