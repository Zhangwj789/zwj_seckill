package com.bmw.seckill.service.impl;

import cn.hutool.core.lang.Assert;
import com.bmw.seckill.common.util.bean.CommonQueryBean;
import com.bmw.seckill.dao.SeckillProductsDao;
import com.bmw.seckill.model.SeckillProducts;
import com.bmw.seckill.service.ISeckillProductsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/8
 * 秒杀商品实现
 */
@Service
@Slf4j
public class SeckillProductsServiceImpl implements ISeckillProductsService {
    @Autowired
    private SeckillProductsDao seckillProductsDao;

    @Override
    public List<SeckillProducts> list4Page(SeckillProducts record, CommonQueryBean queryBean) {
        return seckillProductsDao.list4Page(record,queryBean);
    }

    @Override
    public long count(SeckillProducts record) {
        return seckillProductsDao.count(record);
    }

    @Override
    public int insert(SeckillProducts record) {
        return 0;
    }

    @Override
    public Long uniqueInsert(SeckillProducts record) {
        try {
            record.setCreateTime(new Date());
            record.setIsDeleted(0);
            record.setStatus(SeckillProducts.STATUS_IS_OFFLINE);

            SeckillProducts existItem = findByProductperiodKey(record.getProductPeriodKey());
            if(existItem != null){
                return existItem.getId();
            }else {
                seckillProductsDao.insert(record);
            }
        } catch (Exception e) {
            if(e.getMessage().indexOf("Duplicate entry")>=0){
                SeckillProducts existItem = findByProductperiodKey(record.getProductPeriodKey());
                return existItem.getId();
            }else {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public SeckillProducts selectByPrimaryKey(Long id) {
        return seckillProductsDao.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SeckillProducts record) {
        return seckillProductsDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<SeckillProducts> list(SeckillProducts record) {
        return null;
    }

    @Override
    public SeckillProducts findByProductperiodKey(String productPeriodKey) {
        Assert.isTrue(!StringUtils.isEmpty(productPeriodKey));

        SeckillProducts item = new SeckillProducts();
        item.setProductPeriodKey(productPeriodKey);

        List<SeckillProducts> list = seckillProductsDao.list(item);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }else {
            return list.get(0);
        }

    }
}