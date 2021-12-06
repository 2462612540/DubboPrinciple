package com.zhuangxiaoyan.consumer;

import com.zhuangxiaoyan.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@EnableAutoConfiguration
public class LoadBalanceDubboConsumerDemo {

    @Reference(version = "default", loadbalance = "consistenthash")
    private DemoService demoService;

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(LoadBalanceDubboConsumerDemo.class);

        DemoService demoService = context.getBean(DemoService.class);

        // 用来负载均衡
//        for (int i = 0; i < 1000; i++) {
//            System.out.println((demoService.sayHello("周瑜")));
//            try {
//                Thread.sleep(1 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        // 一致性hash算法测试
        for (int i = 0; i < 1000; i++) {
            System.out.println((demoService.sayHello(i % 5 + "周瑜")));
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
