package com.zhuangxiaoyan.protocol.dubbo;

import com.zhuangxiaoyan.framework.Invocation;
import com.zhuangxiaoyan.register.LocalRegister;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * @Classname NettyServerHandler
 * @Description NettyServerHandler
 * @Date 2021/12/15 20:59
 * @Created by xjl
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Invocation invocation = (Invocation) msg;
        Class serviceImpl = LocalRegister.get(invocation.getInterfaceName());
        Method method = serviceImpl.getMethod(invocation.getMethodName(), invocation.getParamTypes());
        Object result = method.invoke(serviceImpl.newInstance(), invocation.getParams());
        System.out.println("netty----------------------" + result.toString());
        ctx.writeAndFlush("Netty:" + result);
    }
}
