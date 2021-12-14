package com.zhuangxiaoyan.register;

import com.zhuangxiaoyan.framework.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname RemoteMapRegister
 * @Description 模拟远程注册中心
 * @Date 2021/12/14 21:54
 * @Created by xjl
 */
public class RemoteMapRegister {

    private static Map<String, List<URL>> REGISTER = new HashMap<>();

    public static void regist(String interfaceName, URL url) {
        List<URL> list = Collections.singletonList(url);
        REGISTER.put(interfaceName, list);
    }

    public static List<URL> get(String interfaceName) {
        return REGISTER.get(interfaceName);
    }

}
