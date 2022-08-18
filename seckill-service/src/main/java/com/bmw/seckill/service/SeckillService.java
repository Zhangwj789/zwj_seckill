package com.bmw.seckill.service;

import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.model.http.SeckillReq;

/**
 * @author zhangwenjuan
 * @date 2022/8/11
 */
public interface SeckillService {
    BaseResponse sOrder(SeckillReq req);

    BaseResponse pOrder(SeckillReq req);

    BaseResponse oOrder(SeckillReq req) throws Exception;

    BaseResponse cOrder(SeckillReq req);
}