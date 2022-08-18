package com.bmw.seckill.service.impl;

import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.common.exception.ErrorMessage;
import com.bmw.seckill.dao.SeckillOrderDao;
import com.bmw.seckill.dao.SeckillProductsDao;
import com.bmw.seckill.dao.SeckillUserDao;
import com.bmw.seckill.model.SeckillOrder;
import com.bmw.seckill.model.SeckillProducts;
import com.bmw.seckill.model.http.SeckillReq;
import com.bmw.seckill.util.Constant;
import com.bmw.seckill.service.SeckillService;
import com.bmw.seckill.util.DecrCacheStockUtil;
import com.bmw.seckill.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/11
 * 秒杀下单实现
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillOrderDao orderDao;

    @Autowired
    private SeckillProductsDao productsDao;

    @Autowired
    private SeckillUserDao userDao;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    DecrCacheStockUtil decrCacheStockUtil;

    /**
     * 普通下单接口
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse sOrder(SeckillReq req) {
        log.info("===【开始调用原始下单接口】===");
        //参数校验
        log.info("===【校验用户信息及商品信息】===");
        BaseResponse paramValidRes = validateParam(req.getProductId(),req.getUserId());
        //判断校验结果是否成功(成功的话，code就是0)
        if(paramValidRes.getCode()!=0){
            //code不是0，则说明校验错误，返回这个函数中相应的返回错误
            return paramValidRes;
        }
        log.info("===【校验参数是否合法】【通过】===");

        Long productId = req.getProductId();
        Long userId = req.getUserId();
        SeckillProducts product = productsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        //扣减库存
        log.info("===【开始扣减库存】===");
        product.setSaled(product.getSaled()+1);
        product.setSaled(product.getCount()-1);
        productsDao.updateByPrimaryKeySelective(product);
        log.info("===【扣减库存】【成功】===");
        //创建订单
        log.info("===【开始创建订单】===");
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        orderDao.insert(order);
        return BaseResponse.OK(Boolean.TRUE);
    }

    /**
     * 悲观锁实现方式（避免超卖和一人多单）
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse pOrder(SeckillReq req) {
        log.info("===[开始调用秒杀接口(悲观锁)]===");
        //校验用户信息、商品信息、库存信息
        log.info("===[校验用户信息、商品信息、库存信息]===");
        BaseResponse paramValidRes = validateParamPessimistic(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验][通过]===");

        log.info("===【校验 用户是否重复下单】===");
//通过悲观锁 for update锁住整个seckill_order表,需要对字段加上索引（product_id,user_id）
        List<SeckillOrder> repList = orderDao.listRepeatOrdersForUpdate(req.getUserId(),req.getProductId());
        if(repList.size()>0){
            log.error("===【该用户重复下单!】===");
            return BaseResponse.error(ErrorMessage.REPEAT_ORDER_ERROR);
        }
//通过在悲观锁避免超卖代码里，进行常规避免一人多单操作
//        SeckillOrder param = new SeckillOrder();
//        param.setProductId(req.getProductId());
//        param.setUserId(req.getUserId());
//        int repeatCount = orderDao.count(param);
//        if(repeatCount > 0){
//            log.error("===【该用户重复下单!】===");
//            return BaseResponse.error(ErrorMessage.REPEAT_ORDER_ERROR);
//        }
        log.info("===【校验 用户是否重复下单】【通过校验】===");

        Long userId = req.getUserId();
        Long productId = req.getProductId();
        SeckillProducts product = productsDao.selectByPrimaryKey(productId);
        // 下单逻辑
        log.info("===[开始下单逻辑]===");
        Date date = new Date();
        // 扣减库存
        product.setSaled(product.getSaled() + 1);
        productsDao.updateByPrimaryKeySelective(product);
        // 创建订单
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        orderDao.insert(order);
        return BaseResponse.OK;
    }
    // 悲观锁实现的校验逻辑
    private BaseResponse validateParamPessimistic(Long productId, Long userId) {
        //悲观锁，利用selectForUpdate方法锁定记录，并获得最新的SeckillProducts记录
        SeckillProducts product = productsDao.selectForUpdate(productId);
        if (product == null) {
            log.error("===[产品不存在！]===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }
        if (product.getStartBuyTime().getTime() > System.currentTimeMillis()) {
            log.error("===[秒杀还未开始！]===");
            return BaseResponse.error(ErrorMessage.SECKILL_NOT_START);
        }
        if (product.getSaled() >= product.getCount()) {
            log.error("===[库存不足！]===");
            return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
        }
        return BaseResponse.OK;
    }

    //校验用户信息和商品信息
    private BaseResponse validateParam(Long productId,Long userId){
        SeckillProducts product = productsDao.selectByPrimaryKey(productId);
        if(product == null){
            log.error("===【产品不存在】===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }

        if(product.getStartBuyTime().getTime()>System.currentTimeMillis()){
            log.error("===【秒杀还未开始】===");
            return BaseResponse.error(ErrorMessage.SECKILL_NOT_START);
        }

        if(product.getSaled() >= product.getCount()){
            log.error("===【库存不足！】===");
            return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
        }

        return BaseResponse.OK;
    }

    /**
     * 乐观锁（避免超卖和一人多单）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse oOrder(SeckillReq req) throws Exception{
        log.info("===[开始调用下单接口~（乐观锁）]===");
        //参数校验
        log.info("===[校验参数是否合法]===");
        BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验参数是否合法][通过]===");
        //下单（乐观锁）
        return createOptimisticOrder(req.getProductId(), req.getUserId());
    }
    private BaseResponse createOptimisticOrder(Long productId, Long userId) throws Exception {
        log.info("===[下单逻辑Starting]===");
        // 创建订单
        SeckillProducts products = productsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(products.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        orderDao.insert(order);
        log.info("===[创建订单成功]===");
        //扣减库存
        int res = productsDao.updateStockByOptimistic(productId);
        if (res == 0) {
            log.error("===[秒杀失败，抛出异常，执行回滚逻辑！]===");
            throw new Exception("库存不足");
        }
        log.info("===[扣减库存成功!]===");

        try {
            addOrderUserCache(productId,userId);
        } catch (Exception e) {
            log.error("===【记录已购用户缓存是发生异常】===");
        }
        return BaseResponse.OK;
    }

    //将已购买的用户和其商品id存入redis的hset中，方便之后查询，就不用到数据库查询了
   public void addOrderUserCache(Long productId,Long userId){
        String key = String.format(Constant.redisKey.SECKILL_ORDERED_USER,productId);
        redisUtil.sSet(key,userId);
        log.info("===【已将已购用户放入缓存】===");
   }

   public Boolean hasOrderedUserCache(Long productId,Long userId){
        String key = String.format(Constant.redisKey.SECKILL_ORDERED_USER,productId);
        redisUtil.sHasKey(key,userId);
       return null;
   }

   @Override
   @Transactional(rollbackFor = Exception.class)
   public BaseResponse cOrder(SeckillReq req) throws Exception{
        log.info("===【开始调用下单接口~—（避免超卖-Redis）】===");
        long res = 0;
       try {
           log.info("===【校验用户信息、商品信息、库存信息】===");
           BaseResponse paramValidRes = validateParam(req.getProductId(),req.getUserId());
           if (paramValidRes.getCode() != 0) {
               return paramValidRes;
           }
           log.info("===[校验][通过]===");

           Long productId = req.getProductId();
           Long userId = req.getUserId();

           //redis + lua
           res = decrCacheStockUtil.decrStock(req.getProductId());
           if(res == 2){
               //扣减完的库存只要大于等于0，就说明扣减成功
               //开始数据库扣减库存逻辑
               productsDao.decrStock(productId);
               SeckillProducts product = productsDao.selectByPrimaryKey(productId);
               Date date = new Date();
               SeckillOrder order = new SeckillOrder();
               order.setProductId(productId);
               order.setProductName(product.getName());
               order.setUserId(userId);
               order.setCreateTime(date);
               orderDao.insert(order);
               return BaseResponse.OK;
           }else {
               log.error("===[缓存扣减库存不足！]===");
               return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
           }
       } catch (Exception e) {
           log.error("===[异常！]===", e);
           if (res == 2){
               decrCacheStockUtil.addStock(req.getProductId());
           }
           throw new Exception("异常!");
       }
   }


}