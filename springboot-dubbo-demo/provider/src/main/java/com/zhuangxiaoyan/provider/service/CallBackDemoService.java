package com.zhuangxiaoyan.provider.service;

import com.zhuangxiaoyan.DemoService;
import com.zhuangxiaoyan.DemoServiceListener;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// DemoService的sayHello方法的index=1的参数是回调对象，服务消费者可以调用addListener方法来添加回调对象，服务提供者一旦执行回调对象的方法就会通知给服务消费者

//@Service(version = "callback", methods = {@Method(name = "sayHello", arguments = {@Argument(index = 2, callback = true)})}, callbacks = 3)
public class CallBackDemoService implements DemoService {

    private final Map<String, DemoServiceListener> listeners = new ConcurrentHashMap<String, DemoServiceListener>();

    public CallBackDemoService() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (Map.Entry<String, DemoServiceListener> entry : listeners.entrySet()) {
                        entry.getValue().changed(getChanged(entry.getKey()));
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();

    }

    private String getChanged(String key) {
        return "Changed: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public String sayHello(String name) {
        return null;
    }

    @Override
    public String sayHello(String name, String key, DemoServiceListener callback) {
        System.out.println("执行了回调服务" + name);

        callback.changed("xxxx");

        listeners.put(key, callback);
        URL url = RpcContext.getContext().getUrl();
        return String.format("%s：%s, Hello, %s", url.getProtocol(), url.getPort(), name);  // 正常访问
    }

}
