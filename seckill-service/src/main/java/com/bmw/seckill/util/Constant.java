package com.bmw.seckill.util;

/**
 * @author zhangwenjuan
 * @date 2022/8/16
 */
public interface Constant {
    String FAIL = "FAIL";
    String SUCCESS = "SUCCESS";

    interface redisKey{
        /**
         * 已购买用户名单 + 商品id
         * sk：ou：p：商品id
         */
        String SECKILL_ORDERED_USER = "sk:ou:p:%s";
        String SECKILL_SALED_COUNT = "sk:sc:%s";
    }
}
