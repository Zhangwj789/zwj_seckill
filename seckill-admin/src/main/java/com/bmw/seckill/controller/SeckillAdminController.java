package com.bmw.seckill.controller;

import com.bmw.seckill.model.SeckillAdmin;
import com.bmw.seckill.service.IAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/8
 */
@Controller
@RequestMapping(value = "/admin")
@Slf4j
public class SeckillAdminController {
    @Autowired
    private IAdminService adminService;

    @RequestMapping("/listAdminPage")
    public String listAdminPage(Model model){
        List<SeckillAdmin> list = adminService.listAdmin();
        model.addAttribute("list",list);

        return "adminlistAdminPage";
    }

}