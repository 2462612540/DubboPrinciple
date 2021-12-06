package com.zhuangxiaoyan.provider.service;

import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;

//@Service(interfaceName = "com.tuling.DemoService", version = "generic")
public class GenericDemoService implements GenericService {
    @Override
    public Object $invoke(String s, String[] strings, Object[] objects) throws GenericException {
        System.out.println("执行了generic服务");

        return "执行的方法是" + s;
    }
}
