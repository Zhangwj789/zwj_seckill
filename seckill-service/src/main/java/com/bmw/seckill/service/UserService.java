package com.bmw.seckill.service;

import com.bmw.seckill.model.SeckillUser;

/**
 * @author zhangwenjuan
 * @date 2022/8/6
 */
public interface UserService {

    SeckillUser findByPhone(String phone);
}