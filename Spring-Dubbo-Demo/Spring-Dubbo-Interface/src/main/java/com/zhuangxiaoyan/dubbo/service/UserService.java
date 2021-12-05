package com.zhuangxiaoyan.dubbo.service;

import com.zhuangxiaoyan.dubbo.bean.UserAddress;

import java.util.List;

/**
 * @description
 * @param: null
 * @date: 2021/12/5 15:49
 * @return: 
 * @author: xjl
*/
public interface UserService {
	
	/**
	 * 按照用户id返回所有的收货地址
	 * @param userId
	 * @return
	 */
	public List<UserAddress> getUserAddressList(String userId);

}
