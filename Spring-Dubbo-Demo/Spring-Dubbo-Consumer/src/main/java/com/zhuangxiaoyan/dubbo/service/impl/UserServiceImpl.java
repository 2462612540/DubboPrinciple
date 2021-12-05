package com.zhuangxiaoyan.dubbo.service.impl;


import com.zhuangxiaoyan.dubbo.bean.UserAddress;
import com.zhuangxiaoyan.dubbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserService userService;

    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("用户Id="+userId);
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        return userAddressList;
    }

}
