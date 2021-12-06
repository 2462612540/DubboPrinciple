package com.zhuangxiaoyan.provider.service;

import com.zhuangxiaoyan.DemoService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcContext;

//@Service(version = "default")
public class DefaultDemoService implements DemoService {

    @Override
    public String sayHello(String name) {
        System.out.println("执行了服务" + name);

        URL url = RpcContext.getContext().getUrl();
        return String.format("%s：%s, Hello, %s", url.getProtocol(), url.getPort(), name);  // 正常访问
    }

}
