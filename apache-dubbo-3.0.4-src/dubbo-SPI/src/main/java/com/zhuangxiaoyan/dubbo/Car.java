package com.zhuangxiaoyan.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;

/**
 * @Classname Car
 * @Description TODO
 * @Date 2021/12/8 23:00
 * @Created by xjl
 */
@SPI
public interface Car {

    //    @Adaptive
    public void test(URL url);
}
