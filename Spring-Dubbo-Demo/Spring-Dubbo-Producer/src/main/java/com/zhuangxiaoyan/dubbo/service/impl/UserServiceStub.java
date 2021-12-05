package com.zhuangxiaoyan.dubbo.service.impl;

import com.zhuangxiaoyan.dubbo.bean.UserAddress;
import com.zhuangxiaoyan.dubbo.service.UserService;
import org.springframework.util.StringUtils;

import java.util.List;

public class UserServiceStub implements UserService {

    private final UserService userService;

    /**
     * 传入的是userService远程的代理对象
     *
     * @param userService
     */
    public UserServiceStub(UserService userService) {
        super();
        this.userService = userService;
    }

    public List<UserAddress> getUserAddressList(String userId) {
        // TODO Auto-generated method stub
        System.out.println("UserServiceStub.....");
        if (!StringUtils.isEmpty(userId)) {
            return userService.getUserAddressList(userId);
        }
        return null;
    }

}