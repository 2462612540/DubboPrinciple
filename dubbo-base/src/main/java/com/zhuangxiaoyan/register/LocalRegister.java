package com.zhuangxiaoyan.register;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname LocalRegister
 * @Description 模拟本地注册中心
 * @Date 2021/12/14 21:34
 * @Created by xjl
 */
public class LocalRegister {

    private static Map<String, Class> map = new HashMap<>();

    public static void regist(String interfaceName, Class implClass) {
        map.put(interfaceName, implClass);
    }

    public static Class get(String interfaceName) {
        return map.get(interfaceName);
    }
}
