package com.bmw.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.bmw.seckill.common.base.BaseRequest;
import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.common.entity.CommonWebUser;
import com.bmw.seckill.common.exception.ErrorMessage;
import com.bmw.seckill.model.SeckillUser;
import com.bmw.seckill.model.http.UserReq;
import com.bmw.seckill.model.http.UserResp;
import com.bmw.seckill.service.UserService;
import com.bmw.seckill.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author zhangwenjuan
 * @date 2022/8/6
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    //前台用户权限验证   前缀的通用写法
    private final String  USER_PHONE_CODE_BEFORE = "u:p:c:b";

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 验证手机号是否为注册用户手机号，如果不是报错
     * 生成随机数字，然后调用第三方接口给用户发送短信验证码（第三方一般都有sdk，接口都是http的，即我们可以使用httpcient发送数据）
     * 将随机数保存在redis，给接口调用方返回成功信号
     * @param req
     * @return
     */
    @PostMapping("/getPhoneSmsCode")
    public BaseResponse<Boolean> getPhoneSmsCode(@Valid @RequestBody BaseRequest<UserReq.BaseUserInfo> req) {
        String phone = req.getData().getPhone();
        SeckillUser seckillUser = userService.findByPhone(phone);
        //先判断用户存在
        //接下来是调用第三方http接口发送短信验证码，通过验证码存储在redis中，方便后续判断，此处不展示http接口调用了
        if (seckillUser != null) {
            //短信验证码
            String randomCode = "123456";
            redisUtil.set(USER_PHONE_CODE_BEFORE + phone, randomCode,60*30);
            return BaseResponse.ok(true);
        } else return BaseResponse.ok(false);
    }

    /**
     * 先验证用户输入的短信验证码是否正确
     * 验证成功后删除redis种的短信验证码
     * 生成登录用的token，下发token
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/userPhoneLogin")
    public BaseResponse userPhoneLogin(@Valid @RequestBody BaseResponse<UserReq.LoginUserInfo> req)throws Exception{
        UserReq.LoginUserInfo loginUserInfo = req.getData();

        Object existObj = redisUtil.get(USER_PHONE_CODE_BEFORE + loginUserInfo.getPhone());
        //判断redis中存储的某电话号对应的验证码与用户输入的验证码是否匹配
        if(existObj == null || !existObj.toString().equals(loginUserInfo.getSmsCode())){
            return BaseResponse.error(ErrorMessage.SMSCODE_ERROR);
        }else{
            //验证成功，把redis中的短信验证码删掉
            redisUtil.del(USER_PHONE_CODE_BEFORE + loginUserInfo.getPhone());

            SeckillUser seckillUser = userService.findByPhone(loginUserInfo.getPhone());
            System.out.println(JSON.toJSONString(seckillUser));
            CommonWebUser commonWebUser = new CommonWebUser();
            commonWebUser.setId(seckillUser.getId());
            commonWebUser.setName(seckillUser.getName());
            commonWebUser.setPhone(seckillUser.getPhone());
            commonWebUser.setCreateTime(seckillUser.getCreateTime());
            System.out.println(JSON.toJSONString(commonWebUser));
            String token = UUID.randomUUID().toString().replaceAll("-","");
            //设置token超时时间为一个月，根据实际需求确定
            redisUtil.set(token, JSON.toJSONString(commonWebUser),60*60*24*30);
            UserResp.BaseUserResp resp = new UserResp.BaseUserResp();
            resp.setToken(token);
            return BaseResponse.ok(resp);
        }
    }
}