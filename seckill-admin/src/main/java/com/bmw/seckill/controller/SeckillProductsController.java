package com.bmw.seckill.controller;


import com.bmw.seckill.common.util.bean.CommonQueryBean;
import com.bmw.seckill.model.SeckillProducts;
import com.bmw.seckill.service.ISeckillProductsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/8
 */
@Controller
@RequestMapping(value = "/product")
@Slf4j
public class SeckillProductsController {
    @Autowired
    private ISeckillProductsService seckillProductsService;

    /**
     * 分页相关界面
     */
    @RequestMapping("/listPageSeckillProducts")
    public String listPageSeckillProducts(Model model, String name,
                                          @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize){
        SeckillProducts searchItem = new SeckillProducts();
        if(StringUtils.isNotEmpty(name)){
            searchItem.setName(name);
            model.addAttribute("name",name);
        }

        //分页相关，将分页参数封装到queryBean，并且调用分页查询方法，将实体和queryBean两个参数传入
        CommonQueryBean queryBean = new CommonQueryBean();
        queryBean.setPageSize(pageSize);
        queryBean.setStart((pageNum-1)*pageSize);
        List<SeckillProducts> seckillProductsList = seckillProductsService.list4Page(searchItem,queryBean);

        long total = seckillProductsService.count(searchItem);
        int totalPageNum = (int)total/pageSize;
        if(total%pageSize>0)
            totalPageNum ++;

        model.addAttribute("total",total);//总数
        model.addAttribute("totalPage",totalPageNum);//总页数
        model.addAttribute("list",seckillProductsList);
        model.addAttribute("pageSize",pageSize);//每一页存放大小
        model.addAttribute("pageNum",pageNum);//当前页

        System.out.println("*************************");
        return "product/listPageSeckillProducts";
    }

    /**
     * 输入创建product元素的界面
     */
    @RequestMapping("/beforeCreateProduct")
    public String beforeCreateProduct(){
        return "product/beforeCreateProduct";
    }

    @RequestMapping("/doCreateProduct")
    public String doCreateProduct(String uniqueId,String name,String startBuyTimeStr,Integer amount,String desc)throws Exception{
        Assert.notNull(uniqueId,"uniqueId is not null");
        Assert.notNull(name,"name is not null");
        Assert.notNull(startBuyTimeStr,"startBuyTimeStr is not null");
        Assert.notNull(amount,"amount is not null");

        SeckillProducts seckillProducts = new SeckillProducts();
        seckillProducts.setName(name);
        seckillProducts.setStartBuyTime(DateUtils.parseDate(startBuyTimeStr, new String[]{"yyyy-MM-dd HH:mm:SS"}));
        seckillProducts.setCount(amount);
        seckillProducts.setProductPeriodKey(uniqueId);
        if(StringUtils.isNotEmpty(desc))
            seckillProducts.setProductPeriodKey(desc);

        seckillProductsService.uniqueInsert(seckillProducts);
        return "redirect:listPageSeckillProducts?isSave=yes";
    }

    /**
     * 进入更新product的界面
     */
    @RequestMapping("/beforeUpdateProduct")
    public String beforeProduct(Model model,Long id){
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        if(seckillProducts != null){
            model.addAttribute("item",seckillProducts);
        }

        return "product/beforeUpdateProduct";
    }

    /**
     * 实际创建或更新product的业务逻辑
     */
    @RequestMapping("doUpdateProduct")
    public String doUpdateProduct(Long id,String name,String startBuyTimeStr,Integer amount,String desc){
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);

        if(StringUtils.isNotEmpty(name))
            seckillProducts.setName(name);
        if(StringUtils.isNotEmpty(startBuyTimeStr)) {
            seckillProducts.setStartBuyTime(DateUtils.parseDate(startBuyTimeStr, new String[]{"yyyy-MM-dd HH-mm-SS"}));
        }
        if (amount != null)
            seckillProducts.setCount(amount);
        if(StringUtils.isNotEmpty(desc))
            seckillProducts.setProductDesc(desc);

        seckillProducts.setUpdatedTime(new Date());

        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);

        return "redirect:listPageSeckillProducts";
    }

    /**
     * 查看product的元素信息，详情页面中所有元素都展示
     */
    @RequestMapping("/showProductItem")
    public String showProductItem(Model model,Long id){
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);

        if(seckillProducts != null){
            model.addAttribute("item",seckillProducts);
        }
        return "product/showProductItem";
    }

    /**
     * 更新productStatus状态
     */
    @RequestMapping("updateProductStatus")
    public String updateProductStatus(Long id,Integer status){
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        seckillProducts.setStatus(status);
        //模拟逻辑删除又修改回来
        if (SeckillProducts.STATUS_IS_ONLINE.equals(status) && SeckillProducts.IS_DEALED.equals(seckillProducts.getIsDeleted())) {
            seckillProducts.setIsDeleted(0);
        }
        seckillProducts.setUpdatedTime(new Date());
        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);

        return "redirect:listPageSeckillProducts";
    }

    /**
     * 逻辑删除相关秒杀product的信息
     */
    @RequestMapping("/deleteProduct")
    public String deleteProduct(Long id){
        //不直接使用物理删除，改成更新isDelete字段，是逻辑删除
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        seckillProducts.setIsDeleted(SeckillProducts.IS_DEALED);
        seckillProducts.setUpdatedTime(new Date());
        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);

        return "redirect:listPageSeckillProducts";
    }
}