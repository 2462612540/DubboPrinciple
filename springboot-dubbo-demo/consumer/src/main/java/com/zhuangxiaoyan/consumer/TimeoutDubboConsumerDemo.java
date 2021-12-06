package com.zhuangxiaoyan.consumer;

import com.zhuangxiaoyan.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@EnableAutoConfiguration
public class TimeoutDubboConsumerDemo {

    @Reference(version = "timeout", timeout = 3000)
    private DemoService demoService;

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(TimeoutDubboConsumerDemo.class);

        DemoService demoService = context.getBean(DemoService.class);

        // 服务调用超时时间为1秒，默认为3秒
        // 如果这1秒内没有收到服务结果，则会报错
        System.out.println((demoService.sayHello("周瑜"))); //xxservestub

    }

}
