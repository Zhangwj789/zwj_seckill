package com.bmw.seckill.security.other;

import com.bmw.seckill.security.manager.AdminUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author zhangwenjuan
 * @date 2022/8/9
 */
public class AdminUserUtil {
    /**
     * 获取当前用户
     *
     * @return
     */
    public static AdminUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                return null;
            }
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                return (AdminUser) authentication.getPrincipal();
            }
        }
        return null;
    }

}