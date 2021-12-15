package com.zhuangxiaoyan.provider;

import com.zhuangxiaoyan.framework.Protocol;
import com.zhuangxiaoyan.framework.ProtocolFactory;
import com.zhuangxiaoyan.framework.URL;
import com.zhuangxiaoyan.provider.api.HelloService;
import com.zhuangxiaoyan.provider.impl.HelloServiceImpl;
import com.zhuangxiaoyan.register.LocalRegister;
import com.zhuangxiaoyan.register.RemoteMapRegister;

/**
 * @Classname Provider
 * @Description TODO
 * @Date 2021/12/14 20:59
 * @Created by xjl
 */
public class Provider {
    public static void main(String[] args) {
        //1 本地注册
        //{服务名：实现类}
        LocalRegister.regist(HelloService.class.getName(), HelloServiceImpl.class);

        //2 远程注册中心
        //{服务名：list<URL>}
        URL url = new URL("localhost", 8080);
        RemoteMapRegister.regist(HelloService.class.getName(), url);

        //3 启动tomcat
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.steret(url);

    }
}
