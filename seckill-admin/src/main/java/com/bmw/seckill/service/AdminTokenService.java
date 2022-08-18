package com.bmw.seckill.service;

import com.bmw.seckill.security.manager.AdminUser;
import com.bmw.seckill.security.token.Token;

/**
 * @author zhangwenjuan
 * @date 2022/8/9
 */
public interface AdminTokenService {
    AdminUser getLoginUser(String token);
    Token saveToken(AdminUser loginUser);
    void deleteToken(String token);
    void refresh(AdminUser loginUser);
}