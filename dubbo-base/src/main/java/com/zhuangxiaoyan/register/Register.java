package com.zhuangxiaoyan.register;

/**
 * @Classname Register
 * @Description 注册中心接口
 * @Date 2021/12/14 22:20
 * @Created by xjl
 */
public interface Register {

    public void regist(String interfaceName, Class implClass);

    public Class get(String interfaceName);
}
