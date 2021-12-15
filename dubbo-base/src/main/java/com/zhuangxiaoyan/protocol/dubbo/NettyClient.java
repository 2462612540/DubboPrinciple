package com.zhuangxiaoyan.protocol.dubbo;



import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Classname NettyClient
 * @Description TODO
 * @Date 2021/12/15 20:57
 * @Created by xjl
 */
public class NettyClient {

    public NettyClientHandler client = null;

    private static Executor executor = Executors.newCachedThreadPool();

}
