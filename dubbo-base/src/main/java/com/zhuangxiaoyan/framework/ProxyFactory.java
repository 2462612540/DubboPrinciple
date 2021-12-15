package com.zhuangxiaoyan.framework;

import com.zhuangxiaoyan.protocol.http.HttpProtocol;
import com.zhuangxiaoyan.register.RemoteMapRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Classname ProxyFactory
 * @Description 代理对象
 * @Date 2021/12/15 20:28
 * @Created by xjl
 */
public class ProxyFactory {

    public static <T> T getProxyFactory(Class interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Protocol Protocol = ProtocolFactory.getProtocol();
                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);
                URL url = RemoteMapRegister.random(interfaceClass.getName());
                String result = Protocol.send(url, invocation);
                return result;
            }
        });
    }
}
