package com.bmw.seckill.dao;

import java.util.List;
import com.bmw.seckill.model.SeckillOrder;
import com.bmw.seckill.common.util.bean.CommonQueryBean;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * SeckillOrder数据库操作接口类
 * 
 **/

@Repository
public interface SeckillOrderDao{


	/**
	 * 
	 * 查询（根据主键ID查询）
	 * 
	 **/
	SeckillOrder  selectByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 删除（根据主键ID删除）
	 * 
	 **/
	int deleteByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 添加
	 * 
	 **/
	int insert( SeckillOrder record );

	/**
	 * 
	 * 修改 （匹配有值的字段）
	 * 
	 **/
	int updateByPrimaryKeySelective( SeckillOrder record );

	/**
	 * 
	 * list分页查询
	 * 
	 **/
	List<SeckillOrder> list4Page ( SeckillOrder record, @Param("commonQueryParam") CommonQueryBean query);

	/**
	 * 
	 * count查询
	 * 
	 **/
	int count ( SeckillOrder record);

	/**
	 * 
	 * list查询
	 * 
	 **/
	List<SeckillOrder> list ( SeckillOrder record);

	List<SeckillOrder> listRepeatOrdersForUpdate(@Param("userId") Long userId, @Param("productId") Long productId);
}