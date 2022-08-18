package com.bmw.seckill.controller;

import com.bmw.seckill.common.base.BaseRequest;
import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.common.entity.CommonWebUser;
import com.bmw.seckill.common.exception.ErrorMessage;
import com.bmw.seckill.model.http.SeckillReq;
import com.bmw.seckill.security.WebUserUtil;
import com.bmw.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author zhangwenjuan
 * @date 2022/8/11
 */
@RestController
@Slf4j
@RequestMapping(value="/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀下单（原始下单逻辑）
     */
    @RequestMapping(value = "/simple/order")
    public BaseResponse sOrder(@Valid @RequestBody BaseRequest<SeckillReq> request){
        //验证token，获取user信息
        CommonWebUser user = WebUserUtil.getLoginUser();
        //如果当前没有登录
        if(Objects.isNull(user)){
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        System.out.println(user);
        //如果不存在，获取
        SeckillReq req = request.getData();
        System.out.println(req);
        req.setUserId(user.getId());
        System.out.println(req);
        return seckillService.sOrder(req);
    }

    /**
     * 秒杀下单（避免超卖问题——JVM锁）
     */
    @RequestMapping(value = "/synchronized/order")
    public synchronized BaseResponse synchronizedOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.sOrder(req);
    }

    /**
     * 秒杀下单（避免超卖问题——悲观锁）
     */
    @RequestMapping(value = "/pessimistic/order")
    public BaseResponse pOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.pOrder(req);
    }

    /**
     * 秒杀下单（避免超卖问题——乐观锁）
     */
    @RequestMapping(value = "/optimistic/order")
    public BaseResponse oOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.oOrder(req);
        } catch (Exception e) {
            log.error("===[秒杀异常！]===", e);
        }
        return BaseResponse.error(ErrorMessage.SYS_ERROR);
    }

    /**
     * 秒杀下单（避免超卖问题——redis缓存库存，保证原子扣减库存）
     */
    @RequestMapping(value = "/cache/order")
    public BaseResponse cOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.cOrder(req);
        } catch (Exception e) {
            log.error("===秒杀异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SYS_ERROR);
    }
}