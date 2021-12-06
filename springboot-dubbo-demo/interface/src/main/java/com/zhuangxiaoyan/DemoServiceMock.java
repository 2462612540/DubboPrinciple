package com.zhuangxiaoyan;

public class DemoServiceMock implements DemoService {

    @Override
    public String sayHello(String name) {

        return "出现Rpc异常，进行了mock";
    }
}
