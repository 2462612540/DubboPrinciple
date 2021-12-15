package com.zhuangxiaoyan.consumer;

import com.zhuangxiaoyan.framework.ProxyFactory;
import com.zhuangxiaoyan.provider.api.HelloService;

/**
 * @Classname Consumer
 * @Description 模拟消费者
 * @Date 2021/12/14 22:19
 * @Created by xjl
 */
public class Consumer {

    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxyFactory(HelloService.class);

        System.out.println(helloService.sayHello("庄小焱……丫头"));
    }
}
