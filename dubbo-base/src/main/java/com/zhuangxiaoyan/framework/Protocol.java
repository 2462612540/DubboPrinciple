package com.zhuangxiaoyan.framework;

/**
 * @Classname Protocol
 * @Description 协议
 * @Date 2021/12/15 21:33
 * @Created by xjl
 */
public interface Protocol {

    public void steret(URL url);

    String send(URL url, Invocation invocation);
}
