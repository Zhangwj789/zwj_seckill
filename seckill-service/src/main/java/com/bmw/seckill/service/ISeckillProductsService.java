package com.bmw.seckill.service;

import com.bmw.seckill.common.util.bean.CommonQueryBean;
import com.bmw.seckill.model.SeckillProducts;

import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/8
 */
public interface ISeckillProductsService {
    /**
     * list分页查询
     * @param record
     * @param queryBean
     * @return
     */
    List<SeckillProducts> list4Page(SeckillProducts record, CommonQueryBean queryBean);

    /**
     * count查询
     * @param record
     * @return
     */
    long count(SeckillProducts record);

    /**
     * 添加
     * @param record
     */
    int insert(SeckillProducts record);

    /**
     * 根据主键ID查询
     * @param id
     * @return
     */
    SeckillProducts selectByPrimaryKey(Long id);

    /**
     * 修改（匹配有值的字段）
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SeckillProducts record);

    /**
     * list查询
     * @param record
     * @return
     */
    List<SeckillProducts> list(SeckillProducts record);

    /**
     * 唯一索引保证新增的数据唯一
     */
    Long uniqueInsert(SeckillProducts record);

    SeckillProducts findByProductperiodKey(String productPeriodKey);
}
