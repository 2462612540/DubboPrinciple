package com.zhuangxiaoyan.protocol.http;

import com.zhuangxiaoyan.framework.Invocation;
import com.zhuangxiaoyan.register.LocalRegister;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Classname HttpServerHandler
 * @Description 处理请求 返回结果
 * @Date 2021/12/14 21:27
 * @Created by xjl
 */
public class HttpServerHandler {

    public void handler(HttpServletRequest req, HttpServletResponse resp) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        InputStream inputStream = req.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputStream);

        Invocation invocation = (Invocation) ois.readObject();

        Class implClass = LocalRegister.get(invocation.getInterfaceName());

        Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParamTypes());
        // 调用方法
        String result = (String) method.invoke(implClass.newInstance(), invocation.getParams());

        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(resp.getOutputStream());
        //objectOutputStream.writeObject(result);
        //objectOutputStream.flush();
        //objectOutputStream.close();

        IOUtils.write(result, resp.getOutputStream());

    }
}
