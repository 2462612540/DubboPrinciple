package com.zhuangxiaoyan.provider.impl;

import com.zhuangxiaoyan.provider.api.HelloService;

/**
 * @Classname HelloServiceImpl
 * @Description TODO
 * @Date 2021/12/14 20:57
 * @Created by xjl
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String username) {
        return "hello" + username;
    }
}
