package com.zhuangxiaoyan.dubbo;

import com.zhuangxiaoyan.dubbo.bean.UserAddress;
import com.zhuangxiaoyan.dubbo.service.impl.UserServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;

public class MainApplication {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("provider.xml");
        UserServiceImpl bean = ioc.getBean(UserServiceImpl.class);
        List<UserAddress> userAddressList = bean.getUserAddressList("1");
        for (UserAddress userAddress : userAddressList) {
            System.out.println(userAddress.toString());
        }
        System.out.println("调用结束");
        System.in.read();
    }
}
