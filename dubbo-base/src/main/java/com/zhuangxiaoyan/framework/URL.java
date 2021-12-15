package com.zhuangxiaoyan.framework;

import java.io.Serializable;

/**
 * @Classname URL
 * @Description
 * @Date 2021/12/14 21:55
 * @Created by xjl
 */
public class URL implements Serializable {

    private String hostname;
    private Integer port;

    public URL(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
