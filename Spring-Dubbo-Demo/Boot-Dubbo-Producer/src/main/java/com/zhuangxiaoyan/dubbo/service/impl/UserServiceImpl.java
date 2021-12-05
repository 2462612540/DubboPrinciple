package com.zhuangxiaoyan.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zhuangxiaoyan.dubbo.bean.UserAddress;
import com.zhuangxiaoyan.dubbo.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Service//暴露服务
@Component
public class UserServiceImpl implements UserService {

    @HystrixCommand
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        // TODO Auto-generated method stub
        System.out.println("UserServiceImpl..3.....");
        UserAddress address1 = new UserAddress(1, "北京市", "1", "李老师", "010-56253825", "Y");
        UserAddress address2 = new UserAddress(2, "深圳市）", "1", "王老师", "010-56253825", "N");

        if (Math.random() > 0.5) {
            throw new RuntimeException();
        }
        return Arrays.asList(address1, address2);
    }

}
