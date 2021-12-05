package com.zhuangxiaoyan.dubbo.service;

import java.util.List;

import com.zhuangxiaoyan.dubbo.bean.UserAddress;
/**
 * @description 
  * @param: null
 * @date: 2021/12/5 15:49
 * @return: 
 * @author: xjl
*/
public interface OrderService {
	
	/**
	 * 初始化订单
	 * @param userId
	 */
	public List<UserAddress> initOrder(String userId);

}
