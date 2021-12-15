package com.zhuangxiaoyan.protocol.http;

import com.zhuangxiaoyan.framework.Invocation;
import com.zhuangxiaoyan.framework.Protocol;
import com.zhuangxiaoyan.framework.URL;

/**
 * @Classname HttpProtocol
 * @Description TODO
 * @Date 2021/12/15 21:37
 * @Created by xjl
 */
public class HttpProtocol implements Protocol {
    @Override
    public void steret(URL url) {
        new HttpServer().start(url.getHostname(), url.getPort());
    }

    @Override
    public String send(URL url, Invocation invocation) {
        return new HttpClient().send(url.getHostname(), url.getPort(), invocation);
    }
}
