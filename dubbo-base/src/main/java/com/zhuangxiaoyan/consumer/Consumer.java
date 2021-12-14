package com.zhuangxiaoyan.consumer;

import com.zhuangxiaoyan.framework.Invocation;
import com.zhuangxiaoyan.protocol.http.HttpClient;
import com.zhuangxiaoyan.provider.api.HelloService;

/**
 * @Classname Consumer
 * @Description 模拟消费者
 * @Date 2021/12/14 22:19
 * @Created by xjl
 */
public class Consumer {

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        Invocation invocation = new Invocation(HelloService.class.getName(), "sayHello", new Class[]{String.class}, new Object[]{"庄小焱"});
        String result = httpClient.send("localhost", 8080, invocation);
        System.out.println(result);
    }
}
