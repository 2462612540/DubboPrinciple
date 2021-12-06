package com.zhuangxiaoyan.consumer;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@EnableAutoConfiguration
public class GenericDubboConsumerDemo {

    @Reference(id = "demoService", version = "default", interfaceName = "com.tuling.DemoService", generic = true)
    private GenericService genericService;

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(GenericDubboConsumerDemo.class);

        GenericService genericService = (GenericService) context.getBean("demoService");

        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"周瑜"});
        System.out.println(result);

    }

}
