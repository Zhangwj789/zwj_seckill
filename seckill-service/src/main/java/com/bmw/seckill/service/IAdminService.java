package com.bmw.seckill.service;

import com.bmw.seckill.model.SeckillAdmin;
import com.bmw.seckill.model.SeckillProducts;

import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/8
 */
public interface IAdminService {
    public List<SeckillAdmin> listAdmin();

    SeckillAdmin findByUsername(String username);
}
