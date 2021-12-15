package com.zhuangxiaoyan.framework;

import com.zhuangxiaoyan.protocol.dubbo.DubboProtocol;
import com.zhuangxiaoyan.protocol.http.HttpProtocol;

/**
 * @Classname ProtocolFactory
 * @Description TODO
 * @Date 2021/12/15 21:42
 * @Created by xjl
 */
public class ProtocolFactory {

    public static Protocol getProtocol() {
        // 工厂模式
        String name = System.getProperty("protocolName");
        if (name == null || name.equals("")) {
            name = "http";
        }
        switch (name) {
            case "http":
                return new HttpProtocol();
            case "dubbo":
                return new DubboProtocol();
            default:
                break;
        }
        return new HttpProtocol();
    }
}
