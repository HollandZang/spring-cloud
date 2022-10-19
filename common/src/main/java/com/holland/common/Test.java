package com.holland.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.vo.Email;

public class Test {
    public String t;
    public Email email;

    public static void main(String[] args) {
        Test test = new Test();
        test.t="--";
        test.email=new Email("++");

        String s = JSON.toJSONString(test);
        System.out.println(s);

        Test test1 = JSON.parseObject(s, Test.class);
        System.out.println(test1);

        System.out.println(test.email.validate());
        test.email=new Email("zhn.pop@gmail.com");
        System.out.println(test.email.validate());

        test.email=new Email(null);
        System.out.println(test.email.cut(19));
    }

    @Override
    public String toString() {
        return "Test{" +
                "t='" + t + '\'' +
                ", email=" + email +
                '}';
    }
}
