package com.zhuangxiaoyan.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.net.MalformedURLException;


/**
 * @Classname SpiTest
 * @Description TODO
 * @Date 2021/12/8 23:00
 * @Created by xjl
 */
public class SpiTest {
    public static void main(String[] args) throws MalformedURLException {
        ExtensionLoader<Car> extensionLoader= ExtensionLoader.getExtensionLoader(Car.class);

        URL url=new URL("http","localhost", 8080);

        Car car=extensionLoader.getExtension("car");
        car.test(null);
    }
}
