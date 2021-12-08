package com.zhuangxiaoyan.dubbo;

import org.apache.dubbo.common.URL;

/**
 * @Classname CarImpl
 * @Description TODO
 * @Date 2021/12/8 23:01
 * @Created by xjl
 */
public class CarImpl implements Car {

//    private Person person;
//
//    public void setPerson(Person person) {
//        this.person = person;
//    }

    @Override
    public void test(URL url) {
        System.out.println("SPI Test ……");
    }
}
